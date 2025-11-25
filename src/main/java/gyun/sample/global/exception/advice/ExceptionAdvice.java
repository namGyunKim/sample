package gyun.sample.global.exception.advice;

import gyun.sample.domain.account.payload.dto.CurrentAccountDTO;
import gyun.sample.global.annotaion.CurrentAccount;
import gyun.sample.global.event.ExceptionEvent;
import gyun.sample.global.exception.BindingException;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.JWTInterceptorException;
import gyun.sample.global.exception.SocialException;
import gyun.sample.global.exception.enums.ErrorCode;
import gyun.sample.global.payload.response.RestApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 전역 예외 처리 (JSON API 및 Thymeleaf 뷰 공통 처리)
 * - Accept 헤더를 감지하여 JSON 또는 HTML로 응답을 분기합니다.
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionAdvice {

    private final ApplicationEventPublisher applicationEventPublisher;

    // =================================================================================
    // Custom Exception Handlers
    // =================================================================================

    @ExceptionHandler(GlobalException.class)
    public Object handleGlobalException(GlobalException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        return processException(e, e.getErrorCode(), HttpStatus.BAD_REQUEST, e.getErrorDetailMessage(), account, request);
    }

    @ExceptionHandler(BindingException.class)
    public Object handleBindingException(BindingException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        // BindingAdvice(AOP)에서 던진 예외를 처리
        // 상세 메시지에 필드별 에러 내용(Map.toString)이 포함됨
        return processException(e, e.getErrorCode(), HttpStatus.BAD_REQUEST, e.getErrorDetailMessage(), account, request);
    }

    @ExceptionHandler(SocialException.class)
    public Object handleSocialException(SocialException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        return processException(e, e.getErrorCode(), HttpStatus.BAD_REQUEST, e.getErrorDetailMessage(), account, request);
    }

    @ExceptionHandler(JWTInterceptorException.class)
    public Object handleJWTException(JWTInterceptorException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        return processException(e, e.getErrorCode(), HttpStatus.UNAUTHORIZED, e.getErrorDetailMessage(), account, request);
    }

    // =================================================================================
    // Standard Exception Handlers
    // =================================================================================

    @ExceptionHandler(AccessDeniedException.class)
    public Object handleAccessDeniedException(AccessDeniedException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        return processException(e, ErrorCode.ACCESS_DENIED, HttpStatus.FORBIDDEN, e.getMessage(), account, request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public Object handleNoResourceFoundException(NoResourceFoundException e, HttpServletRequest request) {
        // 정적 자원 404는 로그 이벤트 발행 제외 (노이즈 감소)
        return createResponse(ErrorCode.PAGE_NOT_EXIST, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public Object handleNoHandlerFoundException(NoHandlerFoundException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        return processException(e, ErrorCode.PAGE_NOT_EXIST, HttpStatus.NOT_FOUND, e.getMessage(), account, request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Object handleMethodNotSupported(HttpRequestMethodNotSupportedException e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        return processException(e, ErrorCode.METHOD_NOT_SUPPORTED, HttpStatus.METHOD_NOT_ALLOWED, e.getMessage(), account, request);
    }

    @ExceptionHandler(Exception.class)
    public Object handleException(Exception e, @CurrentAccount CurrentAccountDTO account, HttpServletRequest request) {
        log.error("Unexpected Error: ", e);
        return processException(e, ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), account, request);
    }

    // =================================================================================
    // Internal Logic
    // =================================================================================

    private Object processException(Exception e, ErrorCode errorCode, HttpStatus status, String detailMessage, CurrentAccountDTO account, HttpServletRequest request) {
        // 1. 로그 이벤트 발행 (비동기 리스너가 처리하여 DB 적재 등 수행)
        applicationEventPublisher.publishEvent(
                ExceptionEvent.createExceptionEvent(e, errorCode, detailMessage, account, request)
        );

        // 2. 응답 생성 (JSON vs View)
        return createResponse(errorCode, status, request);
    }

    private Object createResponse(ErrorCode errorCode, HttpStatus status, HttpServletRequest request) {
        String acceptHeader = request.getHeader("Accept");
        boolean isJsonRequest = acceptHeader != null && acceptHeader.contains(MediaType.APPLICATION_JSON_VALUE);

        // JSON 요청이면 RestApiResponse 반환
        if (isJsonRequest) {
            return ResponseEntity
                    .status(status)
                    .body(RestApiResponse.fail(errorCode.getErrorResponse()));
        } else {
            // 일반 브라우저 요청이면 HTML 에러 페이지 반환
            ModelAndView mav = new ModelAndView("error/error");
            mav.setStatus(status);
            mav.addObject("code", errorCode.getCode());
            mav.addObject("message", errorCode.getErrorMessage());
            mav.addObject("status", status.value());
            mav.addObject("path", request.getRequestURI());
            return mav;
        }
    }
}