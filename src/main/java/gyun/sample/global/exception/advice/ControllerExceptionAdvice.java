package gyun.sample.global.exception.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import gyun.sample.domain.account.payload.dto.CurrentAccountDTO;
import gyun.sample.global.annotaion.CurrentAccount;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.enums.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ControllerExceptionAdvice extends RestApiControllerAdvice {

    public ControllerExceptionAdvice(ObjectMapper objectMapper, ApplicationEventPublisher applicationEventPublisher) {
        super(objectMapper, applicationEventPublisher);
    }

    // 1. 직접 정의한 GlobalException 처리
    @ExceptionHandler(GlobalException.class)
    public String handleGlobalException(GlobalException e, Model model, HttpServletRequest request, @CurrentAccount CurrentAccountDTO account) {
        ErrorCode errorCode = e.getErrorCode();
        model.addAttribute("errorCode", errorCode.getCode());
        model.addAttribute("errorMessage", errorCode.getErrorMessage());
        model.addAttribute("path", request.getRequestURI());
        // GlobalException은 직접 전달
        sendLogEvent(e, account, request);
        return "error/common";
    }

    // 2. 데이터베이스 관련 예외 처리
    @ExceptionHandler(DataAccessException.class)
    public String handleDataAccessException(DataAccessException e, Model model, HttpServletRequest request, @CurrentAccount CurrentAccountDTO account) {
        ErrorCode errorCode = ErrorCode.DATA_ACCESS_ERROR;
        model.addAttribute("errorCode", errorCode.getCode());
        model.addAttribute("errorMessage", errorCode.getErrorMessage());
        model.addAttribute("path", request.getRequestURI());
        // ✅ DataAccessException을 GlobalException으로 감싸서 전달
        sendLogEvent(new GlobalException(errorCode, e), account, request);
        return "error/common";
    }

    // 3. 그 외 예측하지 못한 모든 예외 처리 (Catch-All)
    @ExceptionHandler(Exception.class)
    public String handleAllUncaughtException(Exception e, Model model, HttpServletRequest request, @CurrentAccount CurrentAccountDTO account) {
        ErrorCode errorCode = getErrorCodeByException(e);
        model.addAttribute("errorCode", errorCode.getCode());
        model.addAttribute("errorMessage", errorCode.getErrorMessage());
        model.addAttribute("path", request.getRequestURI());
        // ✅ Exception을 GlobalException으로 감싸서 전달
        sendLogEvent(new GlobalException(errorCode, e), account, request);
        return "error/common";
    }

    private ErrorCode getErrorCodeByException(Exception exception) {
        if (exception instanceof GlobalException) {
            return ((GlobalException) exception).getErrorCode();
        } else if (exception instanceof DataAccessException) {
            return ErrorCode.DATA_ACCESS_ERROR;
        } else if (exception instanceof MaxUploadSizeExceededException) {
            return ErrorCode.MAX_UPLOAD_SIZE_EXCEEDED;
        } else if (exception instanceof MethodArgumentNotValidException) {
            return ErrorCode.INPUT_VALUE_INVALID;
        } else if (exception instanceof NoHandlerFoundException) {
            return ErrorCode.PAGE_NOT_EXIST;
        } else if (exception instanceof HttpRequestMethodNotSupportedException) {
            return ErrorCode.METHOD_NOT_SUPPORTED;
        } else if (exception instanceof IllegalArgumentException) {
            return ErrorCode.ARGUMENT_INVALID;
        } else {
            return ErrorCode.FAILED; // 기본 에러 코드
        }
    }
}