package gyun.sample.global.exception;

import gyun.sample.global.exception.enums.ErrorCode;
import lombok.Getter;

import java.io.PrintWriter;
import java.io.StringWriter;

// 전역 예외 처리 클래스
@Getter
public class GlobalException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String errorDetailMessage;

    public GlobalException(ErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
        this.errorDetailMessage = getStackTraceMessage(this);
    }

    public GlobalException(ErrorCode errorCode, String errorDetailMessage) {
        super(errorDetailMessage); // 부모 생성자에도 메시지 전달 (로그 가독성)
        this.errorCode = errorCode;
        this.errorDetailMessage = errorDetailMessage;
    }

    public GlobalException(ErrorCode errorCode, Exception exception) {
        super(exception);
        this.errorCode = errorCode;
        this.errorDetailMessage = getStackTraceMessage(exception);
    }

    // 스택 트레이스를 문자열로 변환 (디버깅용 상세 메시지)
    private String getStackTraceMessage(Exception exception) {
        StringWriter stringWriter = new StringWriter();
        exception.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}