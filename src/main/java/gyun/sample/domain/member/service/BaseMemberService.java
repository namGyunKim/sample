package gyun.sample.domain.member.service;

import gyun.sample.domain.account.repository.RefreshTokenRepository;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.domain.social.SocialServiceAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class BaseMemberService {

    protected final PasswordEncoder passwordEncoder;
    protected final MemberRepository memberRepository;
    protected final RefreshTokenRepository refreshTokenRepository;
    protected final SocialServiceAdapter socialServiceAdapter;
}
