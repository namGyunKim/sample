package namGyun.sample.global.payload.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RestApiResponse {

    private boolean success;
    private Object data;

    public static RestApiResponse createResponse(boolean success, Object data) {
        return RestApiResponse.builder()
                .success(success)
                .data(data)
                .build();
    }
}
