package gyun.sample.domain.member.service.write;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.aws.enums.ImageType;
import gyun.sample.domain.log.enums.LogType;
import gyun.sample.domain.log.event.MemberActivityEvent;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.entity.MemberImage;
import gyun.sample.domain.member.enums.MemberType;
import gyun.sample.domain.member.payload.request.MemberCreateRequest;
import gyun.sample.domain.member.payload.request.MemberUpdateRequest;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.domain.member.service.read.ReadUserService;
import gyun.sample.domain.s3.adapter.S3ServiceAdapter;
import gyun.sample.domain.s3.enums.UploadDirect;
import gyun.sample.domain.social.google.service.GoogleSocialService;
import gyun.sample.global.payload.response.GlobalCreateResponse;
import gyun.sample.global.payload.response.GlobalInactiveResponse;
import gyun.sample.global.payload.response.GlobalUpdateResponse;
import gyun.sample.global.security.PrincipalDetails;
import gyun.sample.global.utils.UtilService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class WriteUserService extends AbstractWriteMemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final ReadUserService readUserService;
    private final S3ServiceAdapter s3ServiceAdapter;
    private final GoogleSocialService googleSocialService;
    private final ApplicationEventPublisher eventPublisher;
    private final HttpServletRequest httpServletRequest;

    @Override
    public List<AccountRole> getSupportedRoles() {
        return List.of(AccountRole.USER);
    }

    @Override
    public GlobalCreateResponse createMember(MemberCreateRequest request) {
        // 일반 회원 가입 (소셜이 아닌 경우)
        Member createdMember = new Member(request);
        Member member = memberRepository.save(createdMember);
        member.updatePassword(passwordEncoder.encode(request.password()));

        publishLog(member.getLoginId(), member.getId(), LogType.JOIN, "일반 회원 가입");
        return new GlobalCreateResponse(member.getId());
    }

    @Override
    public GlobalUpdateResponse updateMember(MemberUpdateRequest memberUpdateRequest, String loginId) {
        // 더티 체킹을 이용한 업데이트
        Member member = readUserService.getByLoginIdAndRole(loginId, AccountRole.USER);
        member.update(memberUpdateRequest);

        publishLog(member.getLoginId(), member.getId(), LogType.UPDATE, "회원 정보 수정");
        return new GlobalUpdateResponse(member.getId());
    }

    @Override
    public GlobalInactiveResponse deActiveMember(String loginId) {
        Member member = readUserService.getByLoginIdAndRole(loginId, AccountRole.USER);

        // 1. 구글 연동 해제 (토큰 revoke)
        if (member.getMemberType() == MemberType.GOOGLE) {
            googleSocialService.unlink(member);
        }

        // 2. 회원 탈퇴 처리 (Soft Delete: ID/닉네임 변경 + Active 상태 변경)
        member.withdraw();

        // 3. 프로필 이미지 삭제 (S3 및 DB)
        if (!member.getMemberImages().isEmpty()) {
            List<String> fileNames = member.getMemberImages().stream()
                    .filter(mi -> mi.getUploadDirect() == UploadDirect.MEMBER_PROFILE)
                    .map(MemberImage::getFileName)
                    .toList();

            s3ServiceAdapter.getService(UploadDirect.MEMBER_PROFILE)
                    .deleteImages(fileNames, ImageType.MEMBER_PROFILE, member.getId());

            member.getMemberImages().clear();
        }

        publishLog(loginId, member.getId(), LogType.INACTIVE, "회원 탈퇴(Soft Delete) 처리");

        return new GlobalInactiveResponse(member.getId());
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