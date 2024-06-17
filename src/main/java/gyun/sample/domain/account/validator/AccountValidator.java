package gyun.sample.domain.account.validator;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.global.error.enums.ErrorCode;
import gyun.sample.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class AccountValidator {
    private final PasswordEncoder passwordEncoder;

    //    로그인
    public void login(Member member, String password, AccountRole role) {
        boolean matches = passwordEncoder.matches(password, member.getPassword());
        if (!matches){
            throw new GlobalException(ErrorCode.NOT_MATCH_PASSWORD);
        }
    }
}
