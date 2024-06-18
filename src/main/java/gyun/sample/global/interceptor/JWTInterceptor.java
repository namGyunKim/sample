package gyun.sample.global.interceptor;

import gyun.sample.domain.account.payload.response.TokenResponse;
import gyun.sample.global.utils.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.thymeleaf.util.StringUtils;

// JWT 인터셉터
@Component
@RequiredArgsConstructor
@Slf4j
public class JWTInterceptor implements HandlerInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    // JWT 토큰에서 에러가 있을경우 에러 페이지로 리다이렉트
//    ControllerAdvice에서 처리할수가 없어서 인터셉터에서 컨트롤러로 보내서 처리
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        TokenResponse tokenResponse;

        String authorization = request.getHeader("Authorization");
        String bearer;
        if (!StringUtils.isEmpty(authorization)) {
            bearer = authorization.split(" ")[1];
            tokenResponse = jwtTokenProvider.getTokenResponse(bearer);
            if (tokenResponse.errorCode() != null) {
                final String errorCodeNumber = tokenResponse.getErrorCodeNumber();
                response.sendRedirect("/api/account/jwt-error/" + errorCodeNumber);
                return false;
            }
        }
        return true;
    }
}