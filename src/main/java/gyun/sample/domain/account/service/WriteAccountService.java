package gyun.sample.domain.account.service;

import gyun.sample.domain.account.payload.request.AccountLoginRequest;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.global.utils.UtilService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WriteAccountService extends ReadAccountService {


    public WriteAccountService(MemberRepository memberRepository) {
        super(memberRepository);
    }

    //    로그인
    public void login(AccountLoginRequest request) {
        Member member = findByLoginIdAndRole(request.loginId(), request.role());
        UtilService.forceLogin(member);

    }
}
