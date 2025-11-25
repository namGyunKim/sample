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

/**
 * 전역 예외 처리 (JSON API 및 Thymeleaf 뷰 공통 처리)
 * - RestController에는 JSON 응답을, Controller에는 HTML 뷰 응답을 반환하도록 설정
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice // @RestControllerAdvice 대신 @ControllerAdvice 사용
@RequiredArgsConstructor
public class ExceptionAdvice {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final ObjectMapper objectMapper;

    // =================================================================================
    // Internal Helper Methods for Logging and Response Generation (JSON/HTML)
    // =================================================================================

    // Event - Log
    private void sendLogEvent(Exception exception, ErrorCode errorCode, String errorDetailMessage, CurrentAccountDTO account, HttpServletRequest httpServletRequest) {
        applicationEventPublisher.publishEvent(ExceptionEvent.createExceptionEvent(exception, errorCode, errorDetailMessage, account, httpServletRequest));
    }

    private ResponseEntity<RestApiResponse<ErrorResult>> createFailRestResponse(ErrorResult errorResult, HttpStatus status) {
        return ResponseEntity
                .status(status)
                .body(RestApiResponse.fail(errorResult));
    }

    private ModelAndView createFailModelAndView(ErrorCode errorCode, HttpStatus status, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("error/error"); // 공통 에러 페이지 뷰 이름
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

    /**
     * 비즈니스 로직 전역 예외 처리 (GlobalException)
     */
    @ExceptionHandler(value = GlobalException.class)
    public Object handleGlobalException(GlobalException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        sendLogEvent(e, e.getErrorCode(), e.getErrorDetailMessage(), account, request);
        return handleExceptionInternal(e, e.getErrorCode(), HttpStatus.BAD_REQUEST, account, request);
    }

    /**
     * 바인딩 예외 처리 (BindingAdvice에서 던진 예외)
     */
    @ExceptionHandler(value = BindingException.class)
    public Object handleBindingException(BindingException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        sendLogEvent(e, e.getErrorCode(), e.getErrorDetailMessage(), account, request);
        // HTML 요청인 경우 이전 페이지로 리다이렉트하거나 폼을 다시 보여주는 것이 좋지만,
        // 여기서는 임시로 GlobalException과 동일하게 처리합니다.
        return handleExceptionInternal(e, e.getErrorCode(), HttpStatus.BAD_REQUEST, account, request);
    }

    /**
     * JWT 인터셉터 예외 처리 (세션 기반에서는 발생하지 않음, JSON API 호환성을 위해 유지)
     */
    @ExceptionHandler(value = JWTInterceptorException.class)
    public Object handleJWTInterceptorException(JWTInterceptorException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        sendLogEvent(e, e.getErrorCode(), e.getErrorDetailMessage(), account, request);
        return handleExceptionInternal(e, e.getErrorCode(), HttpStatus.UNAUTHORIZED, account, request);
    }

    /**
     * 소셜 로그인 관련 예외 처리
     */
    @ExceptionHandler(value = SocialException.class)
    public Object handleSocialException(SocialException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        sendLogEvent(e, e.getErrorCode(), e.getErrorDetailMessage(), account, request);
        return handleExceptionInternal(e, e.getErrorCode(), HttpStatus.BAD_REQUEST, account, request);
    }

    // =================================================================================
    // Standard Spring Exception Handlers
    // =================================================================================

    /**
     * @Valid 유효성 검사 실패 시 (@RequestBody)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        return handleExceptionInternal(e, ErrorCode.INPUT_VALUE_INVALID, HttpStatus.BAD_REQUEST, account, request);
    }

    /**
     * DB 데이터 접근 에러 (SQL 예외 등)
     */
    @ExceptionHandler(DataAccessException.class)
    public Object handleDataAccessException(DataAccessException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        return handleExceptionInternal(e, ErrorCode.DATA_ACCESS_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, account, request);
    }

    /**
     * 권한 없음 (Security)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public Object handleAccessDeniedException(AccessDeniedException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        return handleExceptionInternal(e, ErrorCode.ACCESS_DENIED, HttpStatus.FORBIDDEN, account, request);
    }

    /**
     * 파일 업로드 용량 초과
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Object handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        return handleExceptionInternal(e, ErrorCode.MAX_UPLOAD_SIZE_EXCEEDED, HttpStatus.PAYLOAD_TOO_LARGE, account, request);
    }

    /**
     * 잘못된 요청 핸들러 (404)
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public Object handleNoHandlerFoundException(NoHandlerFoundException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        return handleExceptionInternal(e, ErrorCode.PAGE_NOT_EXIST, HttpStatus.NOT_FOUND, account, request);
    }

    /**
     * 지원하지 않는 HTTP Method 요청
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Object handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        return handleExceptionInternal(e, ErrorCode.METHOD_NOT_SUPPORTED, HttpStatus.METHOD_NOT_ALLOWED, account, request);
    }

    /**
     * 잘못된 인자값 전달
     */
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
        // 예상치 못한 예외는 500으로 처리
        return handleExceptionInternal(e, ErrorCode.FAILED, HttpStatus.INTERNAL_SERVER_ERROR, account, request);
    }


    /**
     * 공통 예외 처리 내부 로직 (JSON/HTML 분기)
     * - Accept 헤더를 기반으로 JSON 또는 HTML 응답을 생성합니다.
     */
    private Object handleExceptionInternal(Exception e, ErrorCode errorCode, HttpStatus status, CurrentAccountDTO account, HttpServletRequest request) {
        // 예외 로그 이벤트 발생 (GlobalException으로 래핑)
        GlobalException globalException = new GlobalException(errorCode, e);
        sendLogEvent(globalException, errorCode, e.getMessage(), account, request);

        // Accept 헤더 확인 (JSON API 호출인지, HTML 뷰 요청인지)
        String acceptHeader = request.getHeader("Accept");
        if (acceptHeader != null && acceptHeader.contains(MediaType.APPLICATION_JSON_VALUE)) {
            // JSON 응답 (기존 RestControllerAdvice의 로직)
            ErrorResult errorResult = errorCode.getErrorResponse();
            return createFailRestResponse(errorResult, status);
        } else {
            // HTML/View 응답
            return createFailModelAndView(errorCode, status, request);
        }
    }
}