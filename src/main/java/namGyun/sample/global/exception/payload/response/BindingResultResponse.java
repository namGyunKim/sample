package namGyun.sample.global.exception.payload.response;

public record BindingResultResponse(boolean success,String path,String method,String errorCode,String errorMessage,Object content) {
}
