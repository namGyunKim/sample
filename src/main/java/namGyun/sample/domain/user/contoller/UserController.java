package namGyun.sample.domain.user.contoller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller(value = "User Controller")
//@RequestMapping(value = "/user",headers = "X_API_VERSION=1")
@RequestMapping(value = "/user")
//@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    @GetMapping(value = "/index")
    public String index(){
        System.out.println("true = " + true);
        return "user/index";
    }
}
