package gyun.sample.domain.member.service.write;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.aws.enums.ImageType;
import gyun.sample.domain.aws.enums.UploadDirect;
import gyun.sample.domain.aws.service.implement.S3MemberService;
import gyun.sample.domain.log.enums.LogType;
import gyun.sample.domain.log.event.MemberActivityEvent;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.entity.MemberImage;
import gyun.sample.domain.member.enums.MemberType;
import gyun.sample.domain.member.payload.request.MemberCreateRequest;
import gyun.sample.domain.member.payload.request.MemberUpdateRequest;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.domain.member.service.read.ReadUserService;
import gyun.sample.domain.social.google.service.GoogleSocialService;
import gyun.sample.global.payload.response.GlobalCreateResponse;
import gyun.sample.global.payload.response.GlobalInactiveResponse;
import gyun.sample.global.payload.response.GlobalUpdateResponse;
import gyun.sample.global.security.PrincipalDetails;
import gyun.sample.global.utils.UtilService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 유저(USER) 전용 쓰기 서비스
 * 상속(extends) 대신 조합(Injection)을 사용하여 ReadService를 활용합니다.
 * [변경] S3ServiceAdapter 제거 -> S3MemberService 직접 주입 사용
 */
@Service
@Transactional
@RequiredArgsConstructor
public class WriteUserService extends AbstractWriteMemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final ReadUserService readUserService;

    // [수정] 어댑터 대신 구체적인 S3 서비스 주입
    private final S3MemberService s3MemberService;

    private final GoogleSocialService googleSocialService;
    private final ApplicationEventPublisher eventPublisher;
    private final HttpServletRequest httpServletRequest;

    @Override
    public List<AccountRole> getSupportedRoles() {
        return List.of(AccountRole.USER);
    }

    @Override
    public GlobalCreateResponse createMember(MemberCreateRequest request) {
        // 1. 엔티티 생성 (빌더 대신 생성자 사용)
        Member createdMember = new Member(request);

        // 2. 저장
        Member member = memberRepository.save(createdMember);

        // 3. 비밀번호 암호화 (Setter 대신 비즈니스 메서드 사용)
        member.updatePassword(passwordEncoder.encode(request.password()));

        // 4. 로그 발행
        publishLog(member.getLoginId(), member.getId(), LogType.JOIN, "일반 회원 가입");

        return new GlobalCreateResponse(member.getId());
    }

    @Override
    public GlobalUpdateResponse updateMember(MemberUpdateRequest memberUpdateRequest, String loginId) {
        // 1. 조회 (주입받은 ReadService 사용)
        Member member = readUserService.getByLoginIdAndRole(loginId, AccountRole.USER);

        // 2. 변경 감지 (Dirty Checking)
        member.update(memberUpdateRequest);

        // 3. 로그 발행
        publishLog(member.getLoginId(), member.getId(), LogType.UPDATE, "회원 정보 수정");

        return new GlobalUpdateResponse(member.getId());
    }

    @Override
    public GlobalInactiveResponse deActiveMember(String loginId) {
        Member member = readUserService.getByLoginIdAndRole(loginId, AccountRole.USER);

        // 1. 구글 연동 해제
        if (member.getMemberType() == MemberType.GOOGLE) {
            googleSocialService.unlink(member);
        }

        // 2. 회원 탈퇴 처리 (Soft Delete)
        member.withdraw();

        // 3. 프로필 이미지 정리 (S3MemberService 직접 사용)
        if (!member.getMemberImages().isEmpty()) {
            List<String> fileNames = member.getMemberImages().stream()
                    .filter(mi -> mi.getUploadDirect() == UploadDirect.MEMBER_PROFILE)
                    .map(MemberImage::getFileName)
                    .toList();

            // [수정] 어댑터 호출 제거 -> s3MemberService 직접 호출
            s3MemberService.deleteImages(fileNames, ImageType.MEMBER_PROFILE, member.getId());

            member.getMemberImages().clear();
        }

        publishLog(loginId, member.getId(), LogType.INACTIVE, "회원 탈퇴 처리");

        return new GlobalInactiveResponse(member.getId());
    }

    @Override
    public void updateMemberRole(String loginId, AccountRole newRole) {
        Member member = readUserService.getByLoginIdAndRole(loginId, AccountRole.USER);
        AccountRole oldRole = member.getRole();

        member.changeRole(newRole);

        publishLog(member.getLoginId(), member.getId(), LogType.UPDATE, "권한 변경: " + oldRole + " -> " + newRole);
    }

    private void publishLog(String targetId, Long memberId, LogType type, String details) {
        String executorId = getExecutorId(targetId);
        eventPublisher.publishEvent(MemberActivityEvent.of(
                targetId, memberId, executorId, type, details, UtilService.getClientIp(httpServletRequest)
        ));
    }

    private String getExecutorId(String defaultId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof PrincipalDetails principal) {
            return principal.getUsername();
        }
        return defaultId;
    }
}