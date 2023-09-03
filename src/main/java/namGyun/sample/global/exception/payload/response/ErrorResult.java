package namGyun.sample.global.exception.payload.response;

public record ErrorResult(
        String code,
        String errorMessage) {

    public ErrorResult(String code, String errorMessage) {
        this.code = code;
        this.errorMessage = errorMessage;
    }
}
