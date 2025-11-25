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
        // 회원가입은 보통 수행자가 본인이거나 비로그인 상태(시스템)일 수 있음
        // 여기서는 비로그인 상태에서 가입하므로 executorId를 생성된 회원의 ID로 하거나 "SYSTEM"으로 처리
        // 하지만 관리자가 생성해주는 경우도 있으므로 SecurityContext 확인

        Member createdMember = new Member(request.loginId(), request.nickName(), request.memberType(), null);
        Member member = memberRepository.save(createdMember);
        member.updatePassword(passwordEncoder.encode(request.password()));

        String executorId = getExecutorId(member.getLoginId()); // 본인 가입 시 본인 ID

        eventPublisher.publishEvent(MemberActivityEvent.of(
                member.getLoginId(),
                member.getId(),
                executorId,
                LogType.JOIN,
                "일반 회원 가입",
                UtilService.getClientIp(httpServletRequest)
        ));

        return new GlobalCreateResponse(member.getId());
    }

    @Override
    public GlobalUpdateResponse updateMember(MemberUpdateRequest memberUpdateRequest, String loginId) {
        Member member = readUserService.getByLoginIdAndRole(loginId, AccountRole.USER);
        member.update(memberUpdateRequest);

        String executorId = getExecutorId(member.getLoginId()); // 현재 로그인한 사람

        eventPublisher.publishEvent(MemberActivityEvent.of(
                member.getLoginId(),
                member.getId(),
                executorId,
                LogType.UPDATE,
                "회원 정보 수정",
                UtilService.getClientIp(httpServletRequest)
        ));

        return new GlobalUpdateResponse(member.getId());
    }

    @Override
    public GlobalInactiveResponse deActiveMember(String loginId) {
        Member member = readUserService.getByLoginIdAndRole(loginId, AccountRole.USER);

        if (member.getMemberType() == MemberType.GOOGLE) {
            googleSocialService.unlink(member);
        }

        member.deActive();
        member.invalidateRefreshToken();

        if (!member.getMemberImages().isEmpty()) {
            List<String> fileNames = member.getMemberImages().stream()
                    .filter(mi -> mi.getUploadDirect() == UploadDirect.MEMBER_PROFILE)
                    .map(MemberImage::getFileName)
                    .toList();

            s3ServiceAdapter.getService(UploadDirect.MEMBER_PROFILE)
                    .deleteImages(fileNames, ImageType.MEMBER_PROFILE, member.getId());

            member.getMemberImages().clear();
        }

        String executorId = getExecutorId(member.getLoginId());

        eventPublisher.publishEvent(MemberActivityEvent.of(
                member.getLoginId(),
                member.getId(),
                executorId,
                LogType.INACTIVE,
                "회원 탈퇴/비활성화 처리",
                UtilService.getClientIp(httpServletRequest)
        ));

        return new GlobalInactiveResponse(member.getId());
    }

    // 현재 로그인한 사용자 ID 가져오기, 없으면 defaultId 반환
    private String getExecutorId(String defaultId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof PrincipalDetails principal) {
            return principal.getUsername();
        }
        return defaultId; // 로그인 정보가 없으면(회원가입 등) 대상자 본인으로 간주
    }
}