package gyun.sample.domain.member.service.write;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.payload.request.MemberCreateRequest;
import gyun.sample.domain.member.payload.request.MemberUpdateRequest;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.domain.member.service.read.ReadUserService;
import gyun.sample.global.payload.response.GlobalCreateResponse;
import gyun.sample.global.payload.response.GlobalInactiveResponse;
import gyun.sample.global.payload.response.GlobalUpdateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class WriteUserService extends AbstractWriteMemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final ReadUserService readUserService;

    @Override
    public List<AccountRole> getSupportedRoles() {
        return List.of(AccountRole.USER);
    }

    @Override
    public GlobalCreateResponse createMember(MemberCreateRequest request) {
        // Builder 미사용, 생성자 사용
        Member createdMember = new Member(request);

        Member member = memberRepository.save(createdMember);
        // 비밀번호 암호화 (Setter 대신 비즈니스 메서드 사용)
        member.updatePassword(passwordEncoder.encode(request.password()));

        return new GlobalCreateResponse(member.getId());
    }

    @Override
    public GlobalUpdateResponse updateMember(MemberUpdateRequest request, String loginId) {
        // 1. 조회 (영속성 컨텍스트 로드)
        Member member = readUserService.getByLoginIdAndRole(loginId, AccountRole.USER);

        // 2. 변경 (Dirty Checking - 트랜잭션 종료 시 자동 update 쿼리 실행)
        member.update(request);

        if (request.password() != null && !request.password().isBlank()) {
            member.updatePassword(passwordEncoder.encode(request.password()));
        }

        return new GlobalUpdateResponse(member.getId());
    }

    @Override
    public GlobalInactiveResponse deActiveMember(String loginId) {
        Member member = readUserService.getByLoginIdAndRole(loginId, AccountRole.USER);

        // Dirty Checking으로 상태 변경
        member.deActive();

        return new GlobalInactiveResponse(member.getId());
    }
}