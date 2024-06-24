package gyun.sample.global.event;

import gyun.sample.domain.account.dto.CurrentAccountDTO;
import gyun.sample.global.exception.BindingException;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.JWTInterceptorException;
import gyun.sample.global.exception.SocialException;
import gyun.sample.global.exception.enums.ErrorCode;
import gyun.sample.global.exception.payload.response.BindingResultResponse;
import gyun.sample.global.utils.UtilService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// 예외 발생 시, 예외 정보를 담는 이벤트 객체
@Data
@NoArgsConstructor
public class ExceptionEvent {
    private String requestPath;
    private String requestMethod;
    private String errorName;
    private ErrorCode errorCode;
    private String errorDetailMsg;
    private CurrentAccountDTO account;
    private LocalDateTime createdAt;
    private String clientIp;

    // 예외 발생 시, 예외 정보를 담는 이벤트 객체 생성
    public static ExceptionEvent createExceptionEvent(Exception exception, ErrorCode errorCode,
                                                      String errorDetailMsg, CurrentAccountDTO account,
                                                      HttpServletRequest httpServletRequest) {
        ExceptionEvent exceptionEvent = new ExceptionEvent();
        exceptionEvent.setRequestPath(httpServletRequest.getRequestURL().toString());
        exceptionEvent.setRequestMethod(httpServletRequest.getMethod());
        exceptionEvent.setClientIp(UtilService.getClientIp(httpServletRequest));
        exceptionEvent.setErrorName(exception.getClass().getSimpleName());
        exceptionEvent.setErrorCode(errorCode);
        exceptionEvent.setErrorDetailMsg(errorDetailMsg);
        exceptionEvent.setAccount(account);
        exceptionEvent.setCreatedAt(LocalDateTime.now());
        return exceptionEvent;
    }

    // 예외 발생 시, 로그인 계정 데이터를 포함한 예외 정보를 담는 이벤트 객체
    public static ExceptionEvent createExceptionEvent(GlobalException exception, CurrentAccountDTO account, HttpServletRequest httpServletRequest) {
        return createExceptionEvent(exception, exception.getErrorCode(), exception.getErrorDetailMessage(), account, httpServletRequest);
    }


    // 예외 발생 시, 로그인 데이터가 없는 예외 정보를 담는 이벤트 객체 (GUEST 계정)
    public static ExceptionEvent createExceptionEventWithJWT(JWTInterceptorException exception, CurrentAccountDTO account, HttpServletRequest httpServletRequest) {
        return createExceptionEvent(exception, exception.getErrorCode(), exception.getErrorDetailMessage(), account, httpServletRequest);
    }

    // 예외 발생 시, 바인딩 리절트 에러 정보를 담는 이벤트 객체
    public static ExceptionEvent createExceptionEventBinding(BindingResultResponse response, CurrentAccountDTO currentAccountDTO, HttpServletRequest httpServletRequest) {
        BindingException exception = new BindingException(ErrorCode.REQUEST_BINDING_RESULT);
        return createExceptionEvent(exception, ErrorCode.REQUEST_BINDING_RESULT, response.content().toString(), currentAccountDTO, httpServletRequest);
    }

    public static ExceptionEvent createExceptionEventWithSocial(SocialException exception, CurrentAccountDTO account, HttpServletRequest httpServletRequest) {
        return createExceptionEvent(exception, exception.getErrorCode(), exception.getErrorDetailMessage(), account, httpServletRequest);
    }

    // 예외 발생 시, 이벤트 로그 생성
    public String getExceptionString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\nlogStart=== === === === === === === === === === === === === === === === === === === === === === === === === === logStart\n")
                .append("Exception Title : ").append(errorName).append("\n")
                .append("Request Path : ").append(requestPath).append("\n")
                .append("Request Method : ").append(requestMethod).append("\n")
                .append("Client IP : ").append(clientIp).append("\n");

        if (account != null) {
            stringBuilder.append("Account role : ").append(account.role()).append("\n")
                    .append("Account ID : ").append(account.loginId()).append("\n")
                    .append("Account Nickname : ").append(account.nickName()).append("\n");
        }

        if (this.errorCode != null) {
            stringBuilder.append("Error Code & Msg : ").append(errorCode.getCode()).append(" / ").append(errorCode.getErrorMessage()).append("\n");
        }

        stringBuilder.append("createDate : ").append(createdAt.toString()).append("\n\n")
                .append(errorDetailMsg)
                .append("\nlogEnd=== === === === === === === === === === === === === === === === === === === === === === === === === === logEnd\n\n");

        return stringBuilder.toString();
    }
}