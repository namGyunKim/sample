package gyun.sample.domain.account.controller;

import gyun.sample.domain.account.payload.dto.CurrentAccountDTO;
import gyun.sample.global.utils.UtilService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class controller {

    private final UtilService utilService;

    //    로그인 페이지 이동
    @GetMapping("/login")
    public String login(Model model) {
        CurrentAccountDTO currentAccount = utilService.getCurrentAccount();
        model.addAttribute("isLogin", !currentAccount.loginId().equals("GUEST"));
        return "account/login";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "account/access-denied";
    }
}
