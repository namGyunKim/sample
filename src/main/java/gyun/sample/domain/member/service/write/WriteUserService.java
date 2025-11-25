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
import gyun.sample.global.utils.UtilService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher eventPublisher; // [추가] 이벤트 발행기
    private final HttpServletRequest httpServletRequest; // [추가] IP 획득용

    @Override
    public List<AccountRole> getSupportedRoles() {
        return List.of(AccountRole.USER);
    }

    @Override
    public GlobalCreateResponse createMember(MemberCreateRequest request) {

        Member createdMember = new Member(request.loginId(), request.nickName(), request.memberType(), null);

        Member member = memberRepository.save(createdMember);
        member.updatePassword(passwordEncoder.encode(request.password()));

        // [추가] 회원가입 로그 이벤트 발행
        eventPublisher.publishEvent(MemberActivityEvent.of(
                member.getLoginId(),
                member.getId(),
                LogType.JOIN,
                "일반 회원 가입",
                UtilService.getClientIp(httpServletRequest)
        ));

        return new GlobalCreateResponse(member.getId());
    }

    @Override
    public GlobalUpdateResponse updateMember(MemberUpdateRequest memberUpdateRequest, String loginId) {
        // Dirty Checking
        Member member = readUserService.getByLoginIdAndRole(loginId, AccountRole.USER);

        // 변경 내용 추적 (간단히 비밀번호 변경 여부만 확인 예시)
        boolean isPasswordChanged = false;
        if (memberUpdateRequest.password() != null && !memberUpdateRequest.password().isBlank()) {
            member.updatePassword(passwordEncoder.encode(memberUpdateRequest.password()));
            isPasswordChanged = true;
        }

        member.update(memberUpdateRequest);

        // [추가] 정보 수정 로그 이벤트 발행
        String details = isPasswordChanged ? "비밀번호 및 정보 수정" : "정보 수정";
        eventPublisher.publishEvent(MemberActivityEvent.of(
                member.getLoginId(),
                member.getId(),
                LogType.UPDATE,
                details,
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

        // [추가] 탈퇴 로그 이벤트 발행
        eventPublisher.publishEvent(MemberActivityEvent.of(
                member.getLoginId(),
                member.getId(),
                LogType.INACTIVE,
                "회원 탈퇴 요청",
                UtilService.getClientIp(httpServletRequest)
        ));

        return new GlobalInactiveResponse(member.getId());
    }
}