package gyun.sample.domain.index.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller(value = "/")
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "chat";
    }
}
