package namGyun.sample.domain.health.api;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthRestController {

    @GetMapping(value = "/health")
    public String health(){
        return "health";
    }
}
