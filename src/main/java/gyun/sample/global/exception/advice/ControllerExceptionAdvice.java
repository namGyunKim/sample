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


// 예외 처리
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ControllerExceptionAdvice extends RestApiControllerAdvice {

    public ControllerExceptionAdvice(ObjectMapper objectMapper, ApplicationEventPublisher applicationEventPublisher) {
        super(objectMapper, applicationEventPublisher);
    }

    @ExceptionHandler(value = GlobalException.class)
    public String handleException(GlobalException exception, Model model, HttpServletRequest request, @CurrentAccount CurrentAccountDTO currentAccountDTO) {
        ErrorCode errorCode = getErrorCodeByException(exception);

        // 에러 정보를 모델에 추가
        model.addAttribute("errorCode", errorCode.getCode());
        model.addAttribute("errorMessage", errorCode.getErrorMessage());
        model.addAttribute("path", request.getRequestURI());
        sendLogEvent(exception, currentAccountDTO, request);

        return "error/common"; // 공통 에러 페이지로 이동
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