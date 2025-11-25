package gyun.sample.domain.member.validator;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.payload.request.MemberUpdateRequest;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.domain.member.service.read.ReadMemberService;
import gyun.sample.global.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberUserUpdateValidator implements Validator {

    private final MemberRepository memberRepository;
    private final ReadMemberService readUserService; // ReadUserService는 ReadMemberService의 구현체로 주입됩니다.

    // [제거] JWT 관련 필드 제거
    // private final HttpServletRequest httpServletRequest;

    @Override
    public boolean supports(Class<?> clazz) {
        return MemberUpdateRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        // CreateMemberRequest 검증
        MemberUpdateRequest request = (MemberUpdateRequest) target;
        validateMemberRequest(request, errors);
    }


    private void validateMemberRequest(MemberUpdateRequest request, Errors errors) {
        // [수정] JWT 대신 Spring Security Context에서 현재 로그인된 사용자의 loginId를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentLoginId = null;

        if (authentication != null && authentication.getPrincipal() instanceof PrincipalDetails principalDetails) {
            currentLoginId = principalDetails.getUsername();
        }

        if (currentLoginId == null) {
            // 인증 정보가 없는 경우 (Security Config에서 막겠지만, 혹시 모를 상황에 대비)
            errors.reject("auth.required", "로그인 정보가 필요합니다.");
            return;
        }

        // 닉네임 중복 검사 (본인 닉네임 제외)
        if (memberRepository.existsByNickNameAndLoginIdNot(request.nickName(), currentLoginId)) {
            errors.rejectValue("nickName", "nickName.duplicate", "이미 등록된 닉네임입니다.");
        }

        // 현재 로그인된 사용자가 User 역할로 존재하는지 확인 (비즈니스 로직에 기반한 검증)
        readUserService.getByLoginIdAndRole(currentLoginId, AccountRole.USER);
    }
}