package gyun.sample.global.interceptor;

import gyun.sample.global.utils.JwtTokenProvider;
import org.springframework.stereotype.Component;

@Component
public class SuperAdminInterceptor extends AbstractRoleInterceptor {

    public SuperAdminInterceptor(JwtTokenProvider jwtTokenProvider) {
        super(jwtTokenProvider);
    }

    @Override
    protected String getRequiredRole() {
        return "SUPER_ADMIN";
    }

    @Override
    protected String getAccessDeniedMessage() {
        return "최고\t관리자만\t가능\t합니다.";
    }
}