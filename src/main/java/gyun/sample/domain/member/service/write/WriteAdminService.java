package gyun.sample.domain.member.service.write;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.payload.request.MemberCreateRequest;
import gyun.sample.domain.member.payload.request.MemberUpdateRequest;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.domain.member.service.read.ReadAdminService;
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
        member.update(memberUpdateRequest);

        if (memberUpdateRequest.password() != null && !memberUpdateRequest.password().isBlank()) {
            member.updatePassword(passwordEncoder.encode(memberUpdateRequest.password()));
        }
        return new GlobalUpdateResponse(member.getId());
    }

    @Override
    public GlobalInactiveResponse deActiveMember(String loginId) {
        List<AccountRole> roles = Arrays.asList(AccountRole.ADMIN, AccountRole.SUPER_ADMIN);
        Member member = readAdminService.getByLoginIdAndRoles(loginId, roles);
        member.deActive();
        return new GlobalInactiveResponse(member.getId());
    }
}