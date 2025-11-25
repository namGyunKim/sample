package gyun.sample.domain.member.service.write;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.aws.enums.ImageType;
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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
    private final S3ServiceAdapter s3ServiceAdapter; // S3 서비스 어댑터 주입
    private final GoogleSocialService googleSocialService; // 구글 소셜 서비스 주입

    @Override
    public List<AccountRole> getSupportedRoles() {
        return List.of(AccountRole.USER);
    }

    @Override
    public GlobalCreateResponse createMember(MemberCreateRequest request) {

        Member createdMember = new Member(request.loginId(), request.nickName(), request.memberType(), null);

        Member member = memberRepository.save(createdMember);
        member.updatePassword(passwordEncoder.encode(request.password()));

        return new GlobalCreateResponse(member.getId());
    }

    @Override
    public GlobalUpdateResponse updateMember(MemberUpdateRequest memberUpdateRequest, String loginId) {
        // Dirty Checking (변경 감지)
        Member member = readUserService.getByLoginIdAndRole(loginId, AccountRole.USER);
        member.update(memberUpdateRequest);

        if (memberUpdateRequest.password() != null && !memberUpdateRequest.password().isBlank()) {
            member.updatePassword(passwordEncoder.encode(memberUpdateRequest.password()));
        }
        return new GlobalUpdateResponse(member.getId());
    }

    /**
     * 일반 사용자 회원 탈퇴 (비활성화) 로직
     * 1. 회원의 active 상태를 INACTIVE로 변경 (Dirty Checking)
     * 2. Refresh Token 무효화
     * 3. S3에 저장된 프로필 이미지 삭제
     * 4. **소셜 회원의 경우 연동 해제 (구글)**
     */
    @Override
    public GlobalInactiveResponse deActiveMember(String loginId) {
        Member member = readUserService.getByLoginIdAndRole(loginId, AccountRole.USER);

        // 1. 소셜 연동 해제 (구글)
        if (member.getMemberType() == MemberType.GOOGLE) {
            googleSocialService.unlink(member);
        }

        // 2. 상태 변경
        member.deActive();

        // 3. Refresh Token 무효화
        member.invalidateRefreshToken();

        // 4. S3 프로필 이미지 삭제 (MemberImage 엔티티를 삭제하고 S3에서도 삭제)
        if (!member.getMemberImages().isEmpty()) {
            List<String> fileNames = member.getMemberImages().stream()
                    .filter(mi -> mi.getUploadDirect() == UploadDirect.MEMBER_PROFILE)
                    .map(MemberImage::getFileName)
                    .toList();

            // S3 삭제
            s3ServiceAdapter.getService(UploadDirect.MEMBER_PROFILE)
                    .deleteImages(fileNames, ImageType.MEMBER_PROFILE, member.getId());

            // DB에서 MemberImage 레코드 삭제 (Cascade 설정에 의해 Entity의 리스트를 clear하면 자동으로 orphan removal)
            member.getMemberImages().clear();
        }

        return new GlobalInactiveResponse(member.getId());
    }
}