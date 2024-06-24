package gyun.sample.domain.member.service.write;


import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.account.repository.RefreshTokenRepository;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.payload.request.admin.CreateMemberRequest;
import gyun.sample.domain.member.payload.request.admin.UpdateMemberRequest;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.domain.member.service.read.ReadUserService;
import gyun.sample.domain.social.SocialServiceAdapter;
import gyun.sample.domain.social.serviece.SocialService;
import gyun.sample.global.payload.response.GlobalCreateResponse;
import gyun.sample.global.payload.response.GlobalInactiveResponse;
import gyun.sample.global.payload.response.GlobalUpdateResponse;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class WriteUserService extends ReadUserService implements WriteMemberService {

    public WriteUserService(PasswordEncoder passwordEncoder, MemberRepository memberRepository, RefreshTokenRepository refreshTokenRepository, SocialServiceAdapter socialServiceAdapter) {
        super(passwordEncoder, memberRepository, refreshTokenRepository, socialServiceAdapter);
    }

    @Override
    @Transactional
    public GlobalCreateResponse createMember(CreateMemberRequest request) {
        Member createdMember = new Member(request);
        Member member = memberRepository.save(createdMember);
        member.updatePassword(passwordEncoder.encode(request.password()));
        return new GlobalCreateResponse(member.getId());
    }

    @Override
    @Transactional
    public GlobalUpdateResponse updateMember(UpdateMemberRequest updateMemberRequest, String loginId) {
        Member member = getByLoginIdAndRole(loginId, AccountRole.USER);
        member.update(updateMemberRequest);

        if (updateMemberRequest.password() != null && !updateMemberRequest.password().isBlank()) {
            member.updatePassword(passwordEncoder.encode(updateMemberRequest.password()));
        }
        return new GlobalUpdateResponse(member.getId());
    }

    @Override
    @Transactional
    public GlobalInactiveResponse inactiveMember(String loginId) {
        Member member = getByLoginIdAndRole(loginId, AccountRole.USER);
        member.inactive();
        refreshTokenRepository.deleteWithLoginId(loginId);
        if (member.getMemberType().checkSocialType()) {
            SocialService socialService = socialServiceAdapter.getService(member.getMemberType());
            socialService.unlink(member.getSocialToken());
        }
        return new GlobalInactiveResponse(member.getId());
    }
}