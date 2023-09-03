package namGyun.sample.global.exception.payload.response;

public record ErrorResult(
        String code,
        String message) {

    public ErrorResult(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
