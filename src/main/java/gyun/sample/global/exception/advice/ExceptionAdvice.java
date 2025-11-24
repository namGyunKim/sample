package gyun.sample.global.exception.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import gyun.sample.domain.account.payload.dto.CurrentAccountDTO;
import gyun.sample.global.annotaion.CurrentAccount;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.JWTInterceptorException;
import gyun.sample.global.exception.SocialException;
import gyun.sample.global.exception.enums.ErrorCode;
import gyun.sample.global.exception.payload.response.ErrorResult;
import gyun.sample.global.payload.response.RestApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
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

    // =================================================================================
    // Custom Exception Handlers
    // =================================================================================

    /**
     * 비즈니스 로직 전역 예외 처리
     */
    @ExceptionHandler(value = GlobalException.class)
    protected ResponseEntity<RestApiResponse<ErrorResult>> handleGlobalException(GlobalException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        sendLogEvent(e, account, request);
        return createFailRestResponse(e.getErrorCode().getErrorResponse());
    }

    /**
     * JWT 인터셉터 예외 처리
     */
    @ExceptionHandler(value = JWTInterceptorException.class)
    protected ResponseEntity<RestApiResponse<ErrorResult>> handleJWTInterceptorException(JWTInterceptorException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        sendLogEvent(e, account, request);
        return createFailRestResponseWithJWT(e.getErrorCode().getErrorResponse());
    }

    /**
     * 소셜 로그인 관련 예외 처리
     */
    @ExceptionHandler(value = SocialException.class)
    protected ResponseEntity<RestApiResponse<ErrorResult>> handleSocialException(SocialException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        sendLogEvent(e, account, request);
        return createFailRestResponse(e.getErrorCode().getErrorResponseWithSocial(e.getErrorDetailMessage()));
    }

    // =================================================================================
    // Standard Spring Exception Handlers
    // =================================================================================

    /**
     * @Valid 유효성 검사 실패 시 (@RequestBody)
     * BindingAdvice가 처리하지 못한 경우를 대비
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<RestApiResponse<ErrorResult>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        // 상세 에러 메시지를 만들고 싶다면 bindingResult를 파싱하여 ErrorResult에 담을 수 있습니다.
        // 현재 구조상으로는 INPUT_VALUE_INVALID로 통일합니다.
        return handleExceptionInternal(e, ErrorCode.INPUT_VALUE_INVALID, account, request);
    }

    /**
     * DB 데이터 접근 에러 (SQL 예외 등)
     */
    @ExceptionHandler(DataAccessException.class)
    protected ResponseEntity<RestApiResponse<ErrorResult>> handleDataAccessException(DataAccessException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        return handleExceptionInternal(e, ErrorCode.DATA_ACCESS_ERROR, account, request);
    }

    /**
     * 권한 없음 (Security)
     */
    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<RestApiResponse<ErrorResult>> handleAccessDeniedException(AccessDeniedException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        return handleExceptionInternal(e, ErrorCode.ACCESS_DENIED, account, request);
    }

    /**
     * 파일 업로드 용량 초과
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    protected ResponseEntity<RestApiResponse<ErrorResult>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        return handleExceptionInternal(e, ErrorCode.MAX_UPLOAD_SIZE_EXCEEDED, account, request);
    }

    /**
     * 잘못된 요청 핸들러 (404)
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    protected ResponseEntity<RestApiResponse<ErrorResult>> handleNoHandlerFoundException(NoHandlerFoundException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        return handleExceptionInternal(e, ErrorCode.PAGE_NOT_EXIST, account, request);
    }

    /**
     * 지원하지 않는 HTTP Method 요청
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<RestApiResponse<ErrorResult>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        return handleExceptionInternal(e, ErrorCode.METHOD_NOT_SUPPORTED, account, request);
    }

    /**
     * 잘못된 인자값 전달
     */
    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<RestApiResponse<ErrorResult>> handleIllegalArgumentException(IllegalArgumentException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        return handleExceptionInternal(e, ErrorCode.ARGUMENT_INVALID, account, request);
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<RestApiResponse<ErrorResult>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        return handleExceptionInternal(e, ErrorCode.JSON_PROCESS_FAIL, account, request);
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<RestApiResponse<ErrorResult>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        return handleExceptionInternal(e, ErrorCode.INVALID_PARAMETER, account, request);
    }


    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<RestApiResponse<ErrorResult>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        return handleExceptionInternal(e, ErrorCode.INVALID_PARAMETER, account, request);
    }


    // =================================================================================
    // Fallback Handler
    // =================================================================================

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<RestApiResponse<ErrorResult>> handleException(Exception e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        return handleExceptionInternal(e, ErrorCode.FAILED, account, request);
    }


    /**
     * 공통 예외 처리 내부 로직
     * GlobalException으로 래핑하여 로그 이벤트를 발생시키고 응답을 생성합니다.
     */
    private ResponseEntity<RestApiResponse<ErrorResult>> handleExceptionInternal(Exception e, ErrorCode errorCode, CurrentAccountDTO account, HttpServletRequest request) {
        GlobalException globalException = new GlobalException(errorCode, e);
        // Event - Log
        sendLogEvent(globalException, account, request);
        return createFailRestResponseWithJWT(errorCode.getErrorResponse());
    }
}