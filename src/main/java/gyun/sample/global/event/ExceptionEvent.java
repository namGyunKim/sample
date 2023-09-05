package gyun.sample.global.event;

import gyun.sample.domain.account.dto.CurrentAccountDTO;
import gyun.sample.global.error.enums.ErrorCode;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.JWTInterceptorException;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ExceptionEvent {

    protected String errorName;
    protected ErrorCode errorCode;
    protected String errorDetailMsg;
    protected CurrentAccountDTO account;
    protected LocalDateTime createdAt;

    public static ExceptionEvent createExceptionEvent(GlobalException exception, CurrentAccountDTO account) {
        ExceptionEvent exceptionEvent = new ExceptionEvent();
        exceptionEvent.setErrorName(exception.getClass().getSimpleName());
        exceptionEvent.setErrorCode(exception.getErrorCode());
        exceptionEvent.setErrorDetailMsg(exception.getErrorDetailMessage());
        exceptionEvent.setAccount(account);
        exceptionEvent.setCreatedAt(LocalDateTime.now());

        return exceptionEvent;
    }

    public static ExceptionEvent createExceptionEventNoAccount(JWTInterceptorException exception) {
        ExceptionEvent exceptionEvent = new ExceptionEvent();
        exceptionEvent.setErrorName(exception.getClass().getSimpleName());
        exceptionEvent.setErrorCode(exception.getErrorCode());
        exceptionEvent.setErrorDetailMsg(exception.getErrorDetailMessage());
        exceptionEvent.setCreatedAt(LocalDateTime.now());
        return exceptionEvent;
    }

    public String getExceptionString() {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("\n === === === === === === === === === === === === === === === === === === === === === === \n\n");
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
        stringBuilder.append("createDate : ").append(createdAt.toString()).append("\n\n");

        // 4. Set Error Detail Msg
        stringBuilder.append(errorDetailMsg);
        stringBuilder.append("\n === === === === === === === === === === === === === === === === === === === === === === \n\n");

        return stringBuilder.toString();
    }

}
