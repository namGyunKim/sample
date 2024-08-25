package gyun.sample.global.exception.payload.response;

public record BindingResultResponse(
        String path,                // 요청 경로
        String method,              // 요청 메소드
        String errorCode,           // 에러 코드
        String errorMessage,        // 에러 메시지
        Object content              // 에러 내용
) {
}
