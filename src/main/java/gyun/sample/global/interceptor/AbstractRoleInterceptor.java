package gyun.sample.global.interceptor;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.account.payload.response.TokenResponse;
import gyun.sample.global.utils.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.thymeleaf.util.StringUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public abstract class AbstractRoleInterceptor implements HandlerInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    protected abstract boolean getRequiredRole(AccountRole accountRole);

    protected abstract String getAccessDeniedMessage();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        TokenResponse tokenResponse;
        String authorization = request.getHeader("Authorization");
        String bearer;
        if (!StringUtils.isEmpty(authorization)) {
            bearer = authorization.split(" ")[1];
            tokenResponse = jwtTokenProvider.getTokenResponse(bearer);
            if (!getRequiredRole(AccountRole.getByName(tokenResponse.role()))) {
                String message = URLEncoder.encode(getAccessDeniedMessage(), StandardCharsets.UTF_8.toString());
                response.sendRedirect("/api/account/access-denied/" + message);
                return false;
            }
        } else {
            String message = URLEncoder.encode(getAccessDeniedMessage(), StandardCharsets.UTF_8.toString());
            response.sendRedirect("/api/account/access-denied/" + message);
            return false;
        }
        return true;
    }
}
