package gyun.sample.global.exception.payload.response;

public record ErrorResult(
        String code,                // 에러 코드
        String errorMessage         // 에러 메시지
) {

    public ErrorResult(String code, String errorMessage) {
        this.code = code;
        this.errorMessage = errorMessage;
    }
}
