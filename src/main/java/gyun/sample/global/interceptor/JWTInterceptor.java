package gyun.sample.global.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import gyun.sample.domain.account.payload.response.TokenResponse;
import gyun.sample.global.exception.enums.ErrorCode;
import gyun.sample.global.exception.payload.response.ErrorResult;
import gyun.sample.global.payload.response.RestApiResponse;
import gyun.sample.global.utils.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

// JWT 인터셉터
@Component
@RequiredArgsConstructor
@Slf4j
public class JWTInterceptor implements HandlerInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    // 개선됨: 직접 new ObjectMapper() 하지 않고 빈을 주입받아 전역 설정 유지
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }

        String authorization = request.getHeader("Authorization");

        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            String bearer = authorization.split(" ")[1];
            TokenResponse tokenResponse = jwtTokenProvider.getTokenResponse(bearer);

            // 토큰 자체에 에러가 있는 경우 (만료, 변조 등)
            if (tokenResponse.errorCode() != null) {
                sendJsonErrorResponse(response, tokenResponse.errorCode());
                return false;
            }
        }
        // 토큰이 없거나 정상이면 컨트롤러/다음 인터셉터로 진입
        // (권한 검사는 LoginInterceptor 등에서 수행)
        return true;
    }

    private void sendJsonErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorResult errorResult = errorCode.getErrorResponse();
        RestApiResponse<ErrorResult> apiResponse = RestApiResponse.fail(errorResult);

        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}