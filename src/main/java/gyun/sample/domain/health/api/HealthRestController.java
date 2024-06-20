package gyun.sample.domain.health.api;


import gyun.sample.global.api.RestApiController;
import gyun.sample.global.utils.UtilService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "HealthRestController", description = "서버 살아있나 확인 api")
@RestController
@RequiredArgsConstructor
public class HealthRestController {

    private final RestApiController restApiController;

    @Operation(summary = "서버 살아있나 확인")
    @GetMapping(value = "/health")
    public String health() {
        return "health";
    }

    @Operation(summary = "모든 이넘 조회")
    @GetMapping(value = "/enums",headers = "X-API-VERSION=1")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<String> allEnums() {
        Map<String, List<Map<String, Object>>> allEnums = UtilService.getAllEnums();
        return restApiController.createRestResponse(allEnums);
    }
}