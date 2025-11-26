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

/**
 * 쓰기 전용 서비스 (CQRS - Write)
 * 데이터 변경(Create, Update, Delete)을 담당하며 기본적으로 @Transactional이 적용됨.
 */
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
        // 1. 엔티티 생성 (빌더 미사용, 생성자 사용)
        Member createdMember = new Member(request);

        // 2. 저장
        Member member = memberRepository.save(createdMember);

        // 3. 비밀번호 암호화 후 업데이트 (Setter 대신 명시적 메서드 사용)
        member.updatePassword(passwordEncoder.encode(request.password()));

        // 4. 로그 이벤트 발행
        publishLog(member.getLoginId(), member.getId(), LogType.JOIN, "일반 회원 가입");

        return new GlobalCreateResponse(member.getId());
    }

    @Override
    public GlobalUpdateResponse updateMember(MemberUpdateRequest memberUpdateRequest, String loginId) {
        // 1. 조회 (영속성 컨텍스트 로드)
        Member member = readUserService.getByLoginIdAndRole(loginId, AccountRole.USER);

        // 2. 변경 감지(Dirty Checking)를 통한 업데이트
        // 엔티티 내의 update 비즈니스 메서드 호출
        member.update(memberUpdateRequest);

        // 3. 로그 발행
        publishLog(member.getLoginId(), member.getId(), LogType.UPDATE, "회원 정보 수정");

        return new GlobalUpdateResponse(member.getId());
    }

    @Override
    public GlobalInactiveResponse deActiveMember(String loginId) {
        Member member = readUserService.getByLoginIdAndRole(loginId, AccountRole.USER);

        // 1. 구글 연동 해제 (소셜 회원인 경우)
        if (member.getMemberType() == MemberType.GOOGLE) {
            googleSocialService.unlink(member);
        }

        // 2. 회원 탈퇴 처리 (Soft Delete: Active -> INACTIVE, 개인정보 마스킹)
        member.withdraw();

        // 3. 프로필 이미지 삭제 (S3 실제 파일 삭제 및 DB 연관 관계 정리)
        if (!member.getMemberImages().isEmpty()) {
            List<String> fileNames = member.getMemberImages().stream()
                    .filter(mi -> mi.getUploadDirect() == UploadDirect.MEMBER_PROFILE)
                    .map(MemberImage::getFileName)
                    .toList();

            // S3에서 파일 삭제
            s3ServiceAdapter.getService(UploadDirect.MEMBER_PROFILE)
                    .deleteImages(fileNames, ImageType.MEMBER_PROFILE, member.getId());

            // 고아 객체 제거(Orphan Removal)를 위해 컬렉션 비우기
            member.getMemberImages().clear();
        }

        publishLog(loginId, member.getId(), LogType.INACTIVE, "회원 탈퇴(Soft Delete) 처리");

        return new GlobalInactiveResponse(member.getId());
    }

    // 로그 발행 헬퍼 메서드
    private void publishLog(String targetId, Long memberId, LogType type, String details) {
        String executorId = getExecutorId(targetId);
        eventPublisher.publishEvent(MemberActivityEvent.of(
                targetId, memberId, executorId, type, details, UtilService.getClientIp(httpServletRequest)
        ));
    }

    // 현재 로그인한 사용자 ID 가져오기 (없으면 본인)
    private String getExecutorId(String defaultId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof PrincipalDetails principal) {
            return principal.getUsername();
        }
        return defaultId;
    }
}