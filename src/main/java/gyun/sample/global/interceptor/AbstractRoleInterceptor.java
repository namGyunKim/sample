package gyun.sample.global.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.account.payload.response.TokenResponse;
import gyun.sample.global.exception.enums.ErrorCode;
import gyun.sample.global.exception.payload.response.ErrorResult;
import gyun.sample.global.payload.response.RestApiResponse;
import gyun.sample.global.utils.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@RequiredArgsConstructor
public abstract class AbstractRoleInterceptor implements HandlerInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    protected abstract boolean getRequiredRole(AccountRole accountRole);

    protected abstract String getAccessDeniedMessage();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // OPTIONS 요청(Preflight)은 통과
        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }

        TokenResponse tokenResponse;
        String authorization = request.getHeader("Authorization");

        boolean isAuthorized = false;

        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            String bearer = authorization.split(" ")[1];
            tokenResponse = jwtTokenProvider.getTokenResponse(bearer);

            // 토큰이 유효하고 권한 검사를 통과했는지 확인
            if (tokenResponse.errorCode() == null && getRequiredRole(AccountRole.getByName(tokenResponse.role()))) {
                isAuthorized = true;
            }
        }

        if (!isAuthorized) {
            sendJsonErrorResponse(response, getAccessDeniedMessage());
            return false;
        }

        return true;
    }

    private void sendJsonErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403 Forbidden
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorResult errorResult = new ErrorResult(ErrorCode.ACCESS_DENIED.getCode(), message);
        RestApiResponse<ErrorResult> apiResponse = RestApiResponse.fail(errorResult);

        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}