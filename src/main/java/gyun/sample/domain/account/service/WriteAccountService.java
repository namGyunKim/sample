package gyun.sample.domain.account.service;

import gyun.sample.domain.account.payload.request.AccountLoginRequest;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.domain.social.SocialServiceAdapter;
import gyun.sample.global.utils.UtilService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WriteAccountService extends ReadAccountService {


    //    utils

    public final SocialServiceAdapter socialServiceAdapter;


    public WriteAccountService(MemberRepository memberRepository, SocialServiceAdapter socialServiceAdapter) {
        super(memberRepository);
        this.socialServiceAdapter = socialServiceAdapter;
    }

    //    로그인
    public void login(AccountLoginRequest request) {
        Member member = findByLoginIdAndRole(request.loginId(), request.role());
        UtilService.forceLogin(member);

    }
}
