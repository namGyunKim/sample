package gyun.sample.domain.member.service;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.account.repository.RefreshTokenRepository;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.domain.social.SocialServiceAdapter;
import gyun.sample.global.enums.GlobalActiveEnums;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@RequiredArgsConstructor
public class BaseMemberService {

    protected final PasswordEncoder passwordEncoder;
    protected final MemberRepository memberRepository;
    protected final RefreshTokenRepository refreshTokenRepository;
    protected final SocialServiceAdapter socialServiceAdapter;

    public boolean existsByRole(AccountRole accountRole) {
        return memberRepository.existsByRole(accountRole);
    }

    public Member getByLoginIdAndRoles(String loginId, List<AccountRole> roles) {
        Member member = memberRepository.findByLoginIdAndRoleIn(loginId, roles).orElseThrow(() -> new GlobalException(ErrorCode.NOT_EXIST_MEMBER));
        validationMember(member);
        return member;
    }

    public Member getByLoginIdAndRole(String loginId, AccountRole role) {
        Member member = memberRepository.findByLoginIdAndRole(loginId, role).orElseThrow(() -> new GlobalException(ErrorCode.NOT_EXIST_MEMBER));
        validationMember(member);
        return member;
    }

    private void validationMember(Member member) {
        if (member.getActive() == GlobalActiveEnums.INACTIVE) throw new GlobalException(ErrorCode.INACTIVE_MEMBER);
    }
}
