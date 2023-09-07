package gyun.sample.domain.health.api;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "HealthRestController", description = "서버 살아있나 확인 api")
@RestController
public class HealthRestController {

    @Operation(summary = "서버 살아있나 확인")
    @GetMapping(value = "/health")
    public String health(){
        return "health";
    }
}
