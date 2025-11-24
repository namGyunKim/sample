package gyun.sample.global.api;

import gyun.sample.global.payload.response.RestApiResponse;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
@NoArgsConstructor
public class RestApiController {

    // 실패 응답
    public <T> ResponseEntity<RestApiResponse<T>> createFailRestResponse(T data) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED) // 상황에 따라 status 파라미터화 가능
                .body(RestApiResponse.fail(data));
    }

    public <T> ResponseEntity<RestApiResponse<T>> createFailRestResponseWithJWT(T data) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(RestApiResponse.fail(data));
    }

    // 성공 응답 (OK 200)
    public <T> ResponseEntity<RestApiResponse<T>> createRestResponse(T data) {
        return ResponseEntity
                .ok(RestApiResponse.success(data));
    }

    // 생성 성공 응답 (CREATED 201)
    public <T> ResponseEntity<RestApiResponse<T>> createSuccessRestResponse(T data) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(RestApiResponse.success(data));
    }
}