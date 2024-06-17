package gyun.sample.domain.member.service;

import gyun.sample.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class BaseMemberService {

    protected final PasswordEncoder passwordEncoder;
    protected final MemberRepository memberRepository;
}
