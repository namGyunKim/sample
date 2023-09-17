package gyun.sample.global.event;

import gyun.sample.domain.account.dto.CurrentAccountDTO;
import gyun.sample.global.error.enums.ErrorCode;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.JWTInterceptorException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

//  예외 발생 시, 예외 정보를 담는 이벤트 객체
@Data
@NoArgsConstructor
public class ExceptionEvent {

    protected String errorName;
    protected ErrorCode errorCode;
    protected String errorDetailMsg;
    protected CurrentAccountDTO account;
    protected LocalDateTime createdAt;
    protected String requestUrl;
    protected String requestMethod;

    //  예외 발생 시, 로그인 계정 데이터를 포함한 예외 정보를 담는 이벤트 객체
    public static ExceptionEvent createExceptionEvent(GlobalException exception, CurrentAccountDTO account, HttpServletRequest httpServletRequest) {
        ExceptionEvent exceptionEvent = new ExceptionEvent();
        exceptionEvent.setErrorName(exception.getClass().getSimpleName());
        exceptionEvent.setErrorCode(exception.getErrorCode());
        exceptionEvent.setErrorDetailMsg(exception.getErrorDetailMessage());
        exceptionEvent.setAccount(account);
        exceptionEvent.setCreatedAt(LocalDateTime.now());
        exceptionEvent.setRequestUrl(httpServletRequest.getRequestURL().toString());
        exceptionEvent.setRequestMethod(httpServletRequest.getMethod());
        return exceptionEvent;
    }

    //  예외 발생 시, 로그인 데이터가 없는 예외 정보를 담는 이벤트 객체 GUEST 계정
    public static ExceptionEvent createExceptionEventNoAccount(JWTInterceptorException exception,HttpServletRequest httpServletRequest) {
        ExceptionEvent exceptionEvent = new ExceptionEvent();
        exceptionEvent.setErrorName(exception.getClass().getSimpleName());
        exceptionEvent.setErrorCode(exception.getErrorCode());
        exceptionEvent.setErrorDetailMsg(exception.getErrorDetailMessage());
        exceptionEvent.setCreatedAt(LocalDateTime.now());
        exceptionEvent.setRequestUrl(httpServletRequest.getRequestURL().toString());
        exceptionEvent.setRequestMethod(httpServletRequest.getMethod());
        return exceptionEvent;
    }

    //  예외 발생 시, 이벤트 로그 생성
    public String getExceptionString() {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("\nlogStart=== === === === === === === === === === === === === === === === === === === === === === === === === === logStart\n\n");
        stringBuilder.append("Exception Title : ").append(errorName).append("\n");

        // 1. Set User Info
        if (account != null) {
            stringBuilder.append("Account role : ").append(account.role()).append("\n");
//            stringBuilder.append("Account Id : ").append(account.getId()).append("\n");
            stringBuilder.append("Account Username : ").append(account.loginId()).append("\n");
        }
        // 2. Set Exception
        if (this.errorCode != null) {
            stringBuilder.append("Error Code & Msg : ").append(errorCode.getCode()).append(" / ").append(errorCode.getErrorMessage()).append("\n");
        }

        // 3. Occur Date
        stringBuilder.append("createDate : ").append(createdAt.toString()).append("\n");

        // 4. Set Request Info
        stringBuilder.append("Request URL : ").append(requestUrl).append("\n");
        stringBuilder.append("Request Method : ").append(requestMethod).append("\n\n");

        // 5. Set Error Detail Msg
        stringBuilder.append(errorDetailMsg);
        stringBuilder.append("\nlogEnd=== === === === === === === === === === === === === === === === === === === === === === === === === === logEnd\n\n");

        return stringBuilder.toString();
    }

}
