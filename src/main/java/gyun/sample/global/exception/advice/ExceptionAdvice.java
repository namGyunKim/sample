package gyun.sample.global.exception.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import gyun.sample.domain.account.payload.dto.CurrentAccountDTO;
import gyun.sample.global.annotaion.CurrentAccount;
import gyun.sample.global.event.ExceptionEvent;
import gyun.sample.global.exception.BindingException;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.JWTInterceptorException;
import gyun.sample.global.exception.SocialException;
import gyun.sample.global.exception.enums.ErrorCode;
import gyun.sample.global.exception.payload.response.ErrorResult;
import gyun.sample.global.payload.response.RestApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 전역 예외 처리 (JSON API 및 Thymeleaf 뷰 공통 처리)
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionAdvice {

    private final ApplicationEventPublisher applicationEventPublisher;

    // =================================================================================
    // Internal Helper Methods
    // =================================================================================

    private void sendLogEvent(Exception exception, ErrorCode errorCode, String errorDetailMessage, CurrentAccountDTO account, HttpServletRequest httpServletRequest) {
        applicationEventPublisher.publishEvent(ExceptionEvent.createExceptionEvent(exception, errorCode, errorDetailMessage, account, httpServletRequest));
    }

    private ResponseEntity<RestApiResponse<ErrorResult>> createFailRestResponse(ErrorResult errorResult, HttpStatus status) {
        return ResponseEntity
                .status(status)
                .body(RestApiResponse.fail(errorResult));
    }

    private ModelAndView createFailModelAndView(ErrorCode errorCode, HttpStatus status, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("error/error");
        mav.setStatus(status);
        mav.addObject("code", errorCode.getCode());
        mav.addObject("message", errorCode.getErrorMessage());
        mav.addObject("timestamp", System.currentTimeMillis());
        mav.addObject("path", request.getRequestURI());
        return mav;
    }

    // =================================================================================
    // Custom Exception Handlers
    // =================================================================================

    @ExceptionHandler(GlobalException.class)
    public Object handleGlobalException(GlobalException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        sendLogEvent(e, e.getErrorCode(), e.getErrorDetailMessage(), account, request);
        return handleExceptionInternal(e, e.getErrorCode(), HttpStatus.BAD_REQUEST, account, request);
    }

    @ExceptionHandler(BindingException.class)
    public Object handleBindingException(BindingException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        sendLogEvent(e, e.getErrorCode(), e.getErrorDetailMessage(), account, request);
        return handleExceptionInternal(e, e.getErrorCode(), HttpStatus.BAD_REQUEST, account, request);
    }

    @ExceptionHandler(JWTInterceptorException.class)
    public Object handleJWTInterceptorException(JWTInterceptorException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        sendLogEvent(e, e.getErrorCode(), e.getErrorDetailMessage(), account, request);
        return handleExceptionInternal(e, e.getErrorCode(), HttpStatus.UNAUTHORIZED, account, request);
    }

    @ExceptionHandler(SocialException.class)
    public Object handleSocialException(SocialException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        sendLogEvent(e, e.getErrorCode(), e.getErrorDetailMessage(), account, request);
        return handleExceptionInternal(e, e.getErrorCode(), HttpStatus.BAD_REQUEST, account, request);
    }

    // =================================================================================
    // Standard Spring Exception Handlers
    // =================================================================================

    /**
     * 정적 리소스(css, js, image 등)를 찾지 못했을 때 발생하는 예외 (Spring Boot 3.2+)
     * - ERROR 로그를 남기지 않고 404 페이지만 반환합니다.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public Object handleNoResourceFoundException(NoResourceFoundException e, HttpServletRequest request) {
        // 불필요한 Error LogEvent 발행 생략
        log.warn("Resource not found: {}", request.getRequestURI());
        ErrorCode errorCode = ErrorCode.FILE_NOT_FOUND;

        String acceptHeader = request.getHeader("Accept");
        if (acceptHeader != null && acceptHeader.contains(MediaType.APPLICATION_JSON_VALUE)) {
            return createFailRestResponse(errorCode.getErrorResponse(), HttpStatus.NOT_FOUND);
        } else {
            return createFailModelAndView(errorCode, HttpStatus.NOT_FOUND, request);
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        return handleExceptionInternal(e, ErrorCode.INPUT_VALUE_INVALID, HttpStatus.BAD_REQUEST, account, request);
    }

    @ExceptionHandler(DataAccessException.class)
    public Object handleDataAccessException(DataAccessException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        return handleExceptionInternal(e, ErrorCode.DATA_ACCESS_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, account, request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Object handleAccessDeniedException(AccessDeniedException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        return handleExceptionInternal(e, ErrorCode.ACCESS_DENIED, HttpStatus.FORBIDDEN, account, request);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Object handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        return handleExceptionInternal(e, ErrorCode.MAX_UPLOAD_SIZE_EXCEEDED, HttpStatus.PAYLOAD_TOO_LARGE, account, request);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public Object handleNoHandlerFoundException(NoHandlerFoundException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        return handleExceptionInternal(e, ErrorCode.PAGE_NOT_EXIST, HttpStatus.NOT_FOUND, account, request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Object handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        return handleExceptionInternal(e, ErrorCode.METHOD_NOT_SUPPORTED, HttpStatus.METHOD_NOT_ALLOWED, account, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Object handleIllegalArgumentException(IllegalArgumentException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        return handleExceptionInternal(e, ErrorCode.ARGUMENT_INVALID, HttpStatus.BAD_REQUEST, account, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Object handleHttpMessageNotReadableException(HttpMessageNotReadableException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        return handleExceptionInternal(e, ErrorCode.JSON_PROCESS_FAIL, HttpStatus.BAD_REQUEST, account, request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Object handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        // Enum 변환 실패 등 파라미터 타입 에러
        return handleExceptionInternal(e, ErrorCode.INVALID_PARAMETER, HttpStatus.BAD_REQUEST, account, request);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Object handleMissingServletRequestParameterException(MissingServletRequestParameterException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        return handleExceptionInternal(e, ErrorCode.INVALID_PARAMETER, HttpStatus.BAD_REQUEST, account, request);
    }

    // =================================================================================
    // Fallback Handler
    // =================================================================================

    @ExceptionHandler(Exception.class)
    public Object handleException(Exception e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        return handleExceptionInternal(e, ErrorCode.FAILED, HttpStatus.INTERNAL_SERVER_ERROR, account, request);
    }

    private Object handleExceptionInternal(Exception e, ErrorCode errorCode, HttpStatus status, CurrentAccountDTO account, HttpServletRequest request) {
        // 일반적인 에러는 로그 이벤트 발행
        GlobalException globalException = new GlobalException(errorCode, e);
        sendLogEvent(globalException, errorCode, e.getMessage(), account, request);

        String acceptHeader = request.getHeader("Accept");
        if (acceptHeader != null && acceptHeader.contains(MediaType.APPLICATION_JSON_VALUE)) {
            ErrorResult errorResult = errorCode.getErrorResponse();
            return createFailRestResponse(errorResult, status);
        } else {
            return createFailModelAndView(errorCode, status, request);
        }
    }
}