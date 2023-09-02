package namGyun.sample.api.user.restcontroller;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "UserRestController", description = "유저 api 컨트롤러")
@RestController(value = "UserRestController Controller")
@RequestMapping(value = "/api/user", headers = "X_API_VERSION=1")
//@SecurityRequirement(name = "Bearer Authentication")
public class UserRestController {

    @GetMapping(value = "/test")
    public String test(){
        System.out.println("true = " + true);
        return "test";
    }
}
