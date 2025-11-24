package gyun.sample.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import gyun.sample.global.exception.enums.ErrorCode;
import gyun.sample.global.exception.payload.response.ErrorResult;
import gyun.sample.global.payload.response.RestApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // 필터에서 저장해둔 예외 코드가 있는지 확인
        ErrorCode errorCode = (ErrorCode) request.getAttribute("exception");

        if (errorCode == null) {
            errorCode = ErrorCode.ACCESS_DENIED; // 또는 로그인 필요 메시지
        }

        sendErrorResponse(response, errorCode);
    }

    private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorResult errorResult = errorCode.getErrorResponse();
        RestApiResponse<ErrorResult> apiResponse = RestApiResponse.fail(errorResult);

        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}