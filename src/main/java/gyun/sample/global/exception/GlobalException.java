package gyun.sample.global.exception;

import gyun.sample.global.error.enums.ErrorCode;
import lombok.Getter;

import java.io.PrintWriter;
import java.io.StringWriter;

// 전역 예외 처리
@Getter
public class GlobalException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String errorDetailMessage;


    public GlobalException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.errorDetailMessage = getStackTraceMessage(this);
    }

    public GlobalException(ErrorCode errorCode, String errorDetailMessage) {
        this.errorCode = errorCode;
        this.errorDetailMessage = errorDetailMessage;
    }

    public GlobalException(ErrorCode errorCode, Exception exception) {
        this.errorCode = errorCode;
        this.errorDetailMessage = getStackTraceMessage(exception);
    }

    private String getStackTraceMessage(Exception exception) {
        StringWriter stringWriter = new StringWriter();
        exception.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

}
