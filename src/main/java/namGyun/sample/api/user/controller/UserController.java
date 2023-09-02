package namGyun.sample.api.user.controller;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller(value = "User Controller")
@RequestMapping(value = "/user", headers = "X_API_VERSION=1")
//@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    @GetMapping(value = "/index")
    public String index(){
        System.out.println("true = " + true);
        return "user/index";
    }
}
