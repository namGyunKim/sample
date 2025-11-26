package gyun.sample.domain.member.service.write;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.aws.enums.ImageType;
import gyun.sample.domain.aws.enums.UploadDirect;
import gyun.sample.domain.aws.service.implement.S3MemberService;
import gyun.sample.domain.log.enums.LogType;
import gyun.sample.domain.log.event.MemberActivityEvent;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.entity.MemberImage;
import gyun.sample.domain.member.payload.request.MemberCreateRequest;
import gyun.sample.domain.member.payload.request.MemberUpdateRequest;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.domain.member.service.read.ReadAdminService;
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

import java.util.Arrays;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class WriteAdminService extends AbstractWriteMemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final ReadAdminService readAdminService;

    // [수정] S3ServiceAdapter 대신 S3MemberService 주입
    private final S3MemberService s3MemberService;

    private final ApplicationEventPublisher eventPublisher;
    private final HttpServletRequest httpServletRequest;

    @Override
    public List<AccountRole> getSupportedRoles() {
        return List.of(AccountRole.ADMIN, AccountRole.SUPER_ADMIN);
    }

    @Override
    public GlobalCreateResponse createMember(MemberCreateRequest request) {

        Member createdMember = new Member(request);

        Member member = memberRepository.save(createdMember);
        member.updatePassword(passwordEncoder.encode(request.password()));
        return new GlobalCreateResponse(member.getId());
    }

    @Override
    public GlobalUpdateResponse updateMember(MemberUpdateRequest memberUpdateRequest, String loginId) {
        // Dirty Checking
        List<AccountRole> roles = Arrays.asList(AccountRole.ADMIN, AccountRole.SUPER_ADMIN);
        Member member = readAdminService.getByLoginIdAndRoles(loginId, roles);

        // 정보 업데이트 (비밀번호 변경 로직 제거됨)
        member.update(memberUpdateRequest);

        return new GlobalUpdateResponse(member.getId());
    }

    /**
     * 관리자/최고 관리자 회원 탈퇴 (비활성화) 로직
     * 1. 회원의 active 상태를 INACTIVE로 변경 및 개인정보 마스킹 (withdraw)
     * 2. S3에 저장된 프로필 이미지 삭제
     */
    @Override
    public GlobalInactiveResponse deActiveMember(String loginId) {
        List<AccountRole> roles = Arrays.asList(AccountRole.ADMIN, AccountRole.SUPER_ADMIN);
        Member member = readAdminService.getByLoginIdAndRoles(loginId, roles);

        // 1. 상태 변경 (Soft Delete 로직 적용 - deActive() 대신 withdraw() 사용)
        member.withdraw();

        // 2. S3 프로필 이미지 삭제
        if (!member.getMemberImages().isEmpty()) {
            List<String> fileNames = member.getMemberImages().stream()
                    .filter(mi -> mi.getUploadDirect() == UploadDirect.MEMBER_PROFILE)
                    .map(MemberImage::getFileName)
                    .toList();

            // [수정] 어댑터 호출 제거 -> s3MemberService 직접 호출
            s3MemberService.deleteImages(fileNames, ImageType.MEMBER_PROFILE, member.getId());

            // DB에서 MemberImage 레코드 삭제 (Cascade 설정에 의해 Entity의 리스트를 clear하면 자동으로 orphan removal)
            member.getMemberImages().clear();
        }

        return new GlobalInactiveResponse(member.getId());
    }

    @Override
    public void updateMemberRole(String loginId, AccountRole newRole) {
        List<AccountRole> roles = Arrays.asList(AccountRole.ADMIN, AccountRole.SUPER_ADMIN);
        Member member = readAdminService.getByLoginIdAndRoles(loginId, roles);
        AccountRole oldRole = member.getRole();
        member.changeRole(newRole);

        publishLog(member.getLoginId(), member.getId(), LogType.UPDATE, "관리자 권한 변경: " + oldRole + " -> " + newRole);
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