package gyun.sample.domain.account.validator;

import gyun.sample.domain.account.validator.utils.AccountValidatorUtil;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Component;


@Component
public class AccountValidator extends AccountValidatorUtil {
    protected final MemberRepository userRepository;
    protected final AccountValidatorUtil accountValidatorUtil;

    public AccountValidator(MemberRepository userRepository, MemberRepository userRepository1, AccountValidatorUtil accountValidatorUtil) {
        super(userRepository);
        this.userRepository = userRepository1;
        this.accountValidatorUtil = accountValidatorUtil;
    }

    //    로그인
    public void login(Member member, String password) {
        passwordCheck(member, password);
    }


    //    토큰 재발급 전에 유효한 회원인지 검증
    public void getJwtTokenByRefreshToken(String loginId) {

    }
}
