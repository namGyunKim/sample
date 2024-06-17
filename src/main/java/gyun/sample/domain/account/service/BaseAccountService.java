package gyun.sample.domain.account.service;

import gyun.sample.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BaseAccountService {

    protected final MemberRepository memberRepository;
}
