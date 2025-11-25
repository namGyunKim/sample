package gyun.sample.domain.member.service.write;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.aws.enums.ImageType;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.entity.MemberImage;
import gyun.sample.domain.member.payload.request.MemberCreateRequest;
import gyun.sample.domain.member.payload.request.MemberUpdateRequest;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.domain.member.service.read.ReadAdminService;
import gyun.sample.domain.s3.adapter.S3ServiceAdapter;
import gyun.sample.domain.s3.enums.UploadDirect;
import gyun.sample.global.payload.response.GlobalCreateResponse;
import gyun.sample.global.payload.response.GlobalInactiveResponse;
import gyun.sample.global.payload.response.GlobalUpdateResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
    private final S3ServiceAdapter s3ServiceAdapter; // S3 서비스 어댑터 주입

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
     * 1. 회원의 active 상태를 INACTIVE로 변경 (Dirty Checking)
     * 2. Refresh Token 무효화
     * 3. S3에 저장된 프로필 이미지 삭제
     */
    @Override
    public GlobalInactiveResponse deActiveMember(String loginId) {
        List<AccountRole> roles = Arrays.asList(AccountRole.ADMIN, AccountRole.SUPER_ADMIN);
        Member member = readAdminService.getByLoginIdAndRoles(loginId, roles);

        // 1. 상태 변경
        member.deActive();

        // 2. Refresh Token 무효화
        member.invalidateRefreshToken();

        // 3. S3 프로필 이미지 삭제
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