package gyun.sample.domain.member.service.write;


import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.account.repository.RefreshTokenRepository;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.payload.request.admin.CreateMemberRequest;
import gyun.sample.domain.member.payload.request.admin.UpdateMemberRequest;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.domain.member.service.BaseMemberService;
import gyun.sample.global.enums.GlobalActiveEnums;
import gyun.sample.global.error.enums.ErrorCode;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.payload.response.GlobalCreateResponse;
import gyun.sample.global.payload.response.GlobalInactiveResponse;
import gyun.sample.global.payload.response.GlobalUpdateResponse;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class WriteAdminService extends BaseMemberService implements WriteMemberService {

    public WriteAdminService(PasswordEncoder passwordEncoder, MemberRepository memberRepository, RefreshTokenRepository refreshTokenRepository) {
        super(passwordEncoder, memberRepository, refreshTokenRepository);
    }

    @Override
    public GlobalCreateResponse createMember(CreateMemberRequest request) {
        Member createdMember = new Member(request);
        Member member = memberRepository.save(createdMember);
        member.updatePassword(passwordEncoder.encode(request.password()));
        return new GlobalCreateResponse(member.getId());
    }

    @Override
    public GlobalUpdateResponse updateMember(UpdateMemberRequest updateMemberRequest, String loginId) {
        List<AccountRole> roles = Arrays.asList(AccountRole.ADMIN, AccountRole.SUPER_ADMIN);
        Member member = memberRepository.findByLoginIdAndRoleIn(loginId, roles).orElseThrow(() -> new GlobalException(ErrorCode.NOT_EXIST_MEMBER));
        if (member.getActive() == GlobalActiveEnums.INACTIVE) throw new GlobalException(ErrorCode.INACTIVE_MEMBER);
        member.update(updateMemberRequest);

        if (updateMemberRequest.password() != null && !updateMemberRequest.password().isBlank()) {
            member.updatePassword(passwordEncoder.encode(updateMemberRequest.password()));
        }
        return new GlobalUpdateResponse(member.getId());
    }

    @Override
    public GlobalInactiveResponse inactiveMember(String loginId) {
        List<AccountRole> roles = Arrays.asList(AccountRole.ADMIN, AccountRole.SUPER_ADMIN);
        Member member = memberRepository.findByLoginIdAndRoleIn(loginId, roles).orElseThrow(() -> new GlobalException(ErrorCode.NOT_EXIST_MEMBER));
        if (member.getActive() == GlobalActiveEnums.INACTIVE) throw new GlobalException(ErrorCode.INACTIVE_MEMBER);
        member.inactive();
        refreshTokenRepository.deleteWithLoginId(loginId);
        return new GlobalInactiveResponse(member.getId());
    }
}
