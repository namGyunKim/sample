package gyun.sample.global.interceptor;

import gyun.sample.global.utils.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.thymeleaf.util.StringUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class JWTInterceptor implements HandlerInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        try {
            // TODO Auto-generated method stub
            String authorization = request.getHeader("Authorization");
            String bearer = "";
            if (!StringUtils.isEmpty(authorization)) {
                bearer = authorization.split(" ")[1];
                jwtTokenProvider.getTokenResponse(bearer);
            }
        } catch (Exception e) {
                response.sendRedirect("/api/account/jwt-error");
        }
        return true;
    }
}