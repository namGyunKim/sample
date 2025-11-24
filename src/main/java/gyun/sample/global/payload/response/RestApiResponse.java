package gyun.sample.global.payload.response;

import lombok.Getter;

// REST API 공통 응답
@Getter
public class RestApiResponse {

    private final boolean success;
    private final Object data;

    // 생성자 private
    private RestApiResponse(boolean success, Object data) {
        this.success = success;
        this.data = data;
    }

    public static RestApiResponse createResponse(boolean success, Object data) {
        return new RestApiResponse(success, data);
    }
}