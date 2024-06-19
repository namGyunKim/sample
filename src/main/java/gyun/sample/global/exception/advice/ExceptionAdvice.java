package gyun.sample.global.exception.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import gyun.sample.domain.account.dto.CurrentAccountDTO;
import gyun.sample.global.annotaion.CurrentAccount;
import gyun.sample.global.error.enums.ErrorCode;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.JWTInterceptorException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


// 예외 처리
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackages = "gyun.sample")
public class ExceptionAdvice extends RestApiControllerAdvice {




    public ExceptionAdvice(ObjectMapper objectMapper, ApplicationEventPublisher applicationEventPublisher) {
        super(objectMapper, applicationEventPublisher);
    }

    // Global Exception Catch
    @ExceptionHandler(value = GlobalException.class)
    protected ResponseEntity<String> processCommonException(GlobalException commonException, @CurrentAccount CurrentAccountDTO account, HttpServletRequest httpServletRequest) {
        ErrorCode errorCode = commonException.getErrorCode();
        // Event - Log
        sendLogEvent(commonException, account,httpServletRequest);
        return createFailRestResponse(errorCode.getErrorResponse());
    }

    // JWT Interceptor Exception Catch
    @ExceptionHandler(value = JWTInterceptorException.class)
    protected ResponseEntity<String> processJWTInterceptorException(JWTInterceptorException jwtInterceptorException,@CurrentAccount CurrentAccountDTO account,HttpServletRequest httpServletRequest) {
        ErrorCode errorCode = jwtInterceptorException.getErrorCode();
        // Event - Log
        sendLogEvent(jwtInterceptorException,account,httpServletRequest);
        return createFailRestResponseWithJWT(errorCode.getErrorResponse());
    }

}
