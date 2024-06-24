package gyun.sample.global.exception.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import gyun.sample.domain.account.dto.CurrentAccountDTO;
import gyun.sample.global.annotaion.CurrentAccount;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.JWTInterceptorException;
import gyun.sample.global.exception.SocialException;
import gyun.sample.global.exception.enums.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.nio.file.AccessDeniedException;


// 예외 처리
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class ExceptionAdvice extends RestApiControllerAdvice {


    public ExceptionAdvice(ObjectMapper objectMapper, ApplicationEventPublisher applicationEventPublisher) {
        super(objectMapper, applicationEventPublisher);
    }

    // Global Exception Catch
    @ExceptionHandler(value = GlobalException.class)
    protected ResponseEntity<String> processCommonException(GlobalException commonException, @CurrentAccount CurrentAccountDTO account, HttpServletRequest httpServletRequest) {
        ErrorCode errorCode = commonException.getErrorCode();
        // Event - Log
        sendLogEvent(commonException, account, httpServletRequest);
        return createFailRestResponse(errorCode.getErrorResponse());
    }

    // JWT Interceptor Exception Catch
    @ExceptionHandler(value = JWTInterceptorException.class)
    protected ResponseEntity<String> processJWTInterceptorException(JWTInterceptorException jwtInterceptorException, @CurrentAccount CurrentAccountDTO account, HttpServletRequest httpServletRequest) {
        ErrorCode errorCode = jwtInterceptorException.getErrorCode();
        // Event - Log
        sendLogEvent(jwtInterceptorException, account, httpServletRequest);
        return createFailRestResponseWithJWT(errorCode.getErrorResponse());
    }

    // Social Exception Catch
    @ExceptionHandler(value = SocialException.class)
    protected ResponseEntity<String> processSocialException(SocialException socialException, @CurrentAccount CurrentAccountDTO account, HttpServletRequest httpServletRequest) {
        ErrorCode errorCode = socialException.getErrorCode();
        // Event - Log
        sendLogEvent(socialException, account, httpServletRequest);
        return createFailRestResponse(errorCode.getErrorResponseWithSocial(socialException.getErrorDetailMessage()));
    }

    @ExceptionHandler(value = Exception.class)
    protected ResponseEntity<String> processException(Exception exception, @CurrentAccount CurrentAccountDTO account, HttpServletRequest httpServletRequest) {
        ErrorCode errorCode;
        if (exception instanceof DataAccessException) {
            errorCode = ErrorCode.DATA_ACCESS_ERROR;
        } else if (exception instanceof AccessDeniedException) {
            errorCode = ErrorCode.ACCESS_DENIED;
        } else if (exception instanceof MaxUploadSizeExceededException) {
            errorCode = ErrorCode.MAX_UPLOAD_SIZE_EXCEEDED;
        } else if (exception instanceof MethodArgumentNotValidException) {
            errorCode = ErrorCode.INVALID_INPUT_VALUE;
        } else if (exception instanceof NoHandlerFoundException) {
            errorCode = ErrorCode.PAGE_NOT_EXIST;
        } else if (exception instanceof HttpRequestMethodNotSupportedException) {
            errorCode = ErrorCode.METHOD_NOT_SUPPORTED;
        } else if (exception instanceof IllegalArgumentException) {
            errorCode = ErrorCode.INVALID_ARGUMENT;
        } else {
            errorCode = ErrorCode.FAILED;
        }

        GlobalException globalException = new GlobalException(errorCode, exception);
        // Event - Log
        sendLogEvent(globalException, account, httpServletRequest);
        return createFailRestResponseWithJWT(errorCode.getErrorResponse());
    }
}
