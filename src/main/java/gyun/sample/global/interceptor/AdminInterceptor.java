package gyun.sample.global.interceptor;

import gyun.sample.global.utils.JwtTokenProvider;
import org.springframework.stereotype.Component;

@Component
public class AdminInterceptor extends AbstractRoleInterceptor {

    public AdminInterceptor(JwtTokenProvider jwtTokenProvider) {
        super(jwtTokenProvider);
    }

    @Override
    protected String getRequiredRole() {
        return "ADMIN";
    }

    @Override
    protected String getAccessDeniedMessage() {
        return "관리자만\t가능\t합니다.";
    }
}