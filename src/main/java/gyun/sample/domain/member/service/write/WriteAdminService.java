package gyun.sample.domain.member.service.write;


import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.payload.request.CreateMemberAdminRequest;
import gyun.sample.domain.member.payload.request.UpdateMemberRequest;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.domain.member.service.read.ReadAdminService;
import gyun.sample.domain.social.SocialServiceAdapter;
import gyun.sample.domain.social.serviece.SocialService;
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
public class WriteAdminService implements WriteMemberService<CreateMemberAdminRequest> {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final SocialServiceAdapter socialServiceAdapter;
    private final ReadAdminService readAdminService;

    @Override
    public GlobalCreateResponse createMember(CreateMemberAdminRequest request) {
        request.generatedWithUser();
        Member createdMember = new Member(request);
        Member member = memberRepository.save(createdMember);
        member.updatePassword(passwordEncoder.encode(request.password()));
        return new GlobalCreateResponse(member.getId());
    }

    @Override
    public GlobalUpdateResponse updateMember(UpdateMemberRequest updateMemberRequest, String loginId) {
        List<AccountRole> roles = Arrays.asList(AccountRole.ADMIN, AccountRole.SUPER_ADMIN);
        Member member = readAdminService.getByLoginIdAndRoles(loginId, roles);
        member.update(updateMemberRequest);

        if (updateMemberRequest.password() != null && !updateMemberRequest.password().isBlank()) {
            member.updatePassword(passwordEncoder.encode(updateMemberRequest.password()));
        }
        return new GlobalUpdateResponse(member.getId());
    }

    @Override
    @Transactional
    public GlobalInactiveResponse deActiveMember(String loginId) {
        List<AccountRole> roles = Arrays.asList(AccountRole.ADMIN, AccountRole.SUPER_ADMIN);
        Member member = readAdminService.getByLoginIdAndRoles(loginId, roles);
        member.deActive();

        if (member.getMemberType().checkSocialType()) {
            SocialService socialService = socialServiceAdapter.getService(member.getMemberType());
            socialService.unlink(member.getSocialToken());
        }

        return new GlobalInactiveResponse(member.getId());
    }
}