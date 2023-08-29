package namGyun.sample.api.user.controller;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "UserController", description = "유저 컨트롤러")
@RestController(value = "User Controller")
@RequestMapping(value = "/api/user", headers = "X_API_VERSION=1")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    @GetMapping(value = "/test")
    public String test(){
        return "test";
    }
}
