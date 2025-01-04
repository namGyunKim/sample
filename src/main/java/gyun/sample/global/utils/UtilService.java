package gyun.sample.global.utils;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.account.payload.dto.CurrentAccountDTO;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.global.enums.GlobalActiveEnums;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.enums.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.Environment;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UtilService {

    private final Environment environment;
    private final MemberRepository memberRepository;

    // 클라이언트의 IP 주소를 가져오는 메소드
    public static String getClientIp(HttpServletRequest request) {
        try {
            String xForwardedForHeader = request.getHeader("X-Forwarded-For");
            if (xForwardedForHeader != null) {
                return xForwardedForHeader.split(",")[0].trim();
            } else {
                return request.getRemoteAddr();
            }
        } catch (Exception e) {
            return "IP를 가져오지 못했습니다.";
        }
    }

    // 로컬 프로필 여부를 확인하는 메서드
    public boolean isLocalProfile() {
        String[] activeProfiles = environment.getActiveProfiles();
        for (String profile : activeProfiles) {
            if (profile.equalsIgnoreCase("local")) {
                return true;
            }
        }
        return false;
    }

    //    모든 이넘 조회
    public static Map<String, List<Map<String, Object>>> getAllEnums() {
        Map<String, List<Map<String, Object>>> enums = new HashMap<>();

        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(Enum.class));
        Set<BeanDefinition> components = provider.findCandidateComponents("gyun.sample");
        for (BeanDefinition component : components) {
            // 제외할 경로 필터링
            if (Objects.requireNonNull(component.getBeanClassName()).startsWith("gyun.sample.global.exception") || component.getBeanClassName().startsWith("gyun.sample.global.error")) {
                continue;
            }

            try {
                Class<?> cls = Class.forName(component.getBeanClassName());
                if (cls.isEnum()) {
                    List<Map<String, Object>> enumValues = new ArrayList<>();
                    for (Object enumConstant : cls.getEnumConstants()) {
                        Map<String, Object> enumData = new HashMap<>();
                        enumData.put("name", ((Enum<?>) enumConstant).name());
                        try {
                            // getValue 메서드를 호출하여 값을 가져옵니다.
                            Object value = cls.getMethod("getValue").invoke(enumConstant);
                            enumData.put("value", value);
                        } catch (Exception e) {
                            log.error("Failed to get value for enum {}: {}", ((Enum<?>) enumConstant).name(), e.getMessage());
                            throw new GlobalException(ErrorCode.REFLECTION_ERROR, "Failed to get value for enum " + ((Enum<?>) enumConstant).name());
                        }
                        enumValues.add(enumData);
                    }
                    enums.put(cls.getSimpleName(), enumValues);
                }
            } catch (ClassNotFoundException e) {
                log.error("Class not found: {}", e.getMessage());
                throw new GlobalException(ErrorCode.REFLECTION_ERROR, e.getMessage());
            }
        }
        return enums;
    }

    public static Pageable getPageable(int page, int size) {
        return PageRequest.of(page - 1, size);
    }

    public static String getKoreanTime() {
        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분");
        return localDateTime.format(formatter);
    }

    public static String removeNonDigits(String input) {
        // 정규 표현식을 사용하여 숫자 이외의 모든 문자를 빈 문자열로 대체
        return input.replaceAll("[^0-9]", "");
    }

    /**
     * 회원을 강제로 로그인 상태로 만드는 유틸 메서드
     *
     * @param member 로그인 처리할 Member
     * @return 로그인 후 생성된 CurrentAccountDTO
     */
    public static CurrentAccountDTO forceLogin(Member member) {
        // 1) 현재 로그인 계정 정보 DTO 생성
        CurrentAccountDTO currentAccountDTO = new CurrentAccountDTO(member);

        // 2) Spring Security 인증 객체 생성
        //    - Principal 은 currentAccountDTO
        //    - Credentials(비밀번호 등)는 null
        //    - Authorities는 member.getRole() 하나만 부여 (예: ROLE_USER)
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        currentAccountDTO,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority(member.getRole().name()))
                );

        // 3) SecurityContext 에 설정 → "로그인" 상태로 만듦
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 필요하다면, 여기서 세션 무효화 혹은 추가 세팅 가능

        // 4) 생성된 DTO 반환 (컨트롤러 / 서비스에서 필요시 활용)
        return currentAccountDTO;
    }

    public CurrentAccountDTO getCurrentAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal().equals("anonymousUser")) {
            return CurrentAccountDTO.generatedGuest();
        }

        String loginId = authentication.getName();
        Member member = getActiveMemberByLoginId(loginId);

        return new CurrentAccountDTO(member);
    }

    /**
     * 로그인된 사용자의 CurrentAccountDTO를 반환하고,
     * 로그인되지 않았다면 Guest 계정 정보를 반환
     */
    public CurrentAccountDTO getLoginDataOrGuest() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 인증 정보가 없거나 anonymous(비로그인) 상태이면 GUEST 반환
        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return CurrentAccountDTO.generatedGuest();
        }

        Member member = memberRepository.findByLoginId(authentication.getName())
                .orElse(null);
        if (member == null) {
            return CurrentAccountDTO.generatedGuest();
        }
        return new CurrentAccountDTO(member);

    }

    private Member getActiveMemberByLoginId(String loginId) {
        return memberRepository.findByLoginIdAndRoleIn(loginId, getAllowedRoles())
                .filter(member -> member.getActive() == GlobalActiveEnums.ACTIVE)
                .orElseThrow(() -> {
                    if (!memberRepository.existsByLoginId(loginId)) {
                        return new GlobalException(ErrorCode.MEMBER_NOT_EXIST, "회원 정보를 찾을 수 없습니다.");
                    }
                    return new GlobalException(ErrorCode.MEMBER_INACTIVE, "비활성화된 계정입니다.");
                });
    }

    private List<AccountRole> getAllowedRoles() {
        return Arrays.asList(AccountRole.ADMIN, AccountRole.SUPER_ADMIN, AccountRole.USER);
    }

    // 날짜를 "yyyy년 MM월 dd일 HH:mm" 형식으로 포맷
    public static String formattedTime(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm");
        return localDateTime.format(formatter);
    }

    public String encodeFileName(String originalFilename) {
        try {
            return URLEncoder.encode(originalFilename, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("파일명 인코딩 실패", e);
        }
    }

}
