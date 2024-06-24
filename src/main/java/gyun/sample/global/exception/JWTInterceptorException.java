package gyun.sample.global.exception;

import gyun.sample.global.exception.enums.ErrorCode;
import lombok.Getter;

import java.io.PrintWriter;
import java.io.StringWriter;

// JWT 인터셉터 예외 처리
@Getter
public class JWTInterceptorException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String errorDetailMessage;


    public JWTInterceptorException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.errorDetailMessage = getStackTraceMessage(this);
    }

    public JWTInterceptorException(ErrorCode errorCode, String errorDetailMessage) {
        this.errorCode = errorCode;
        this.errorDetailMessage = errorDetailMessage;
    }

    public JWTInterceptorException(ErrorCode errorCode, Exception exception) {
        this.errorCode = errorCode;
        this.errorDetailMessage = getStackTraceMessage(exception);
    }

    private String getStackTraceMessage(Exception exception) {
        StringWriter stringWriter = new StringWriter();
        exception.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

}
