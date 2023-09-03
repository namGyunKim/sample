package namGyun.sample.global.exception;

import lombok.Getter;
import namGyun.sample.global.exception.enums.ErrorCode;

import java.io.PrintWriter;
import java.io.StringWriter;

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
