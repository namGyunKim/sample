package gyun.sample.global.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.enums.ErrorCode;
import gyun.sample.global.payload.response.RestApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class RestApiController {

    private final ObjectMapper objectMapper;

    public RestApiController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ResponseEntity<String> createFailRestResponse(Object data) {
        RestApiResponse restApiResponse = RestApiResponse.createResponse(false, data);
        return convertToResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, restApiResponse);
    }

    public ResponseEntity<String> createRestResponse(Object data) {
        RestApiResponse restApiResponse = RestApiResponse.createResponse(true, data);
        return convertToResponseEntity(HttpStatus.OK, restApiResponse);
    }

    public ResponseEntity<String> createSuccessRestResponse(Object data) {
        RestApiResponse restApiResponse = RestApiResponse.createResponse(true, data);
        return convertToResponseEntity(HttpStatus.CREATED, restApiResponse);
    }

    private ResponseEntity<String> convertToResponseEntity(HttpStatus status, RestApiResponse restApiResponse) {
        String responseBody;
        try {
            responseBody = objectMapper.writeValueAsString(restApiResponse);
        } catch (JsonProcessingException exception) {
            throw new GlobalException(ErrorCode.JSON_PROCESS_FAIL, exception);
        }
        return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(responseBody);
    }
}
