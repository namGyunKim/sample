package gyun.sample.global.exception;

import gyun.sample.global.exception.enums.ErrorCode;
import lombok.Getter;

// 전역 예외 처리
@Getter
public class GlobalException extends RuntimeException {

    private final ErrorCode errorCode;

    /**
     * ErrorCode만으로 예외를 생성합니다.
     * 메시지는 ErrorCode의 기본 메시지를 사용합니다.
     */
    public GlobalException(ErrorCode errorCode) {
        super(errorCode.getErrorMessage()); // 부모 클래스에 에러 메시지 전달
        this.errorCode = errorCode;
    }

    /**
     * ErrorCode와 함께 별도의 상세 메시지를 지정합니다.
     */
    public GlobalException(ErrorCode errorCode, String detailMessage) {
        super(detailMessage); // 부모 클래스에 상세 메시지 전달
        this.errorCode = errorCode;
    }

    /**
     * 다른 예외(cause)를 감싸서(Wrapping) 새로운 GlobalException으로 만듭니다.
     * 이 생성자가 가장 중요하며, 예외의 원인을 추적할 수 있게 해줍니다.
     */
    public GlobalException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getErrorMessage(), cause); // 부모 클래스에 메시지와 원인 예외를 함께 전달
        this.errorCode = errorCode;
    }
}