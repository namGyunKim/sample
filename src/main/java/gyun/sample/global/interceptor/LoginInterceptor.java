package gyun.sample.global.interceptor;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.global.utils.JwtTokenProvider;
import org.springframework.stereotype.Component;

@Component
public class LoginInterceptor extends AbstractRoleInterceptor {

    public LoginInterceptor(JwtTokenProvider jwtTokenProvider) {
        super(jwtTokenProvider);
    }

    @Override
    protected boolean getRequiredRole(AccountRole accountRole) {
        return accountRole != AccountRole.GUEST;
    }

    @Override
    protected String getAccessDeniedMessage() {
        return "회원만\t가능\t합니다.";
    }
}