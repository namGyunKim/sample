package gyun.sample.global.payload.response;

public record RestApiResponse<T>(boolean success, T data) {

    // 생성자 private

    public static <T> RestApiResponse<T> createResponse(boolean success, T data) {
        return new RestApiResponse<>(success, data);
    }

    public static <T> RestApiResponse<T> success(T data) {
        return new RestApiResponse<>(true, data);
    }

    public static <T> RestApiResponse<T> fail(T data) {
        return new RestApiResponse<>(false, data);
    }
}