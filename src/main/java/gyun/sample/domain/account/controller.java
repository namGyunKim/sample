package gyun.sample.domain.account;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class controller {

    //    로그인 페이지 이동
    @GetMapping("/login")
    public String login() {
        return "account/login";
    }
}
