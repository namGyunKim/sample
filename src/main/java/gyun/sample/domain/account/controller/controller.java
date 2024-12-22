package gyun.sample.domain.account.controller;

import gyun.sample.domain.account.payload.dto.CurrentAccountDTO;
import gyun.sample.global.utils.UtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class controller {

    private final UtilService utilService;

    //    로그인 페이지 이동
    @GetMapping("/login")
    public String login(Model model) {
        CurrentAccountDTO currentAccount = utilService.getCurrentAccount();
        boolean isLogin = !currentAccount.loginId().equals("GUEST");
        if (isLogin) {
            return "redirect:/";
        }
        return "account/login";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "account/access-denied";
    }
}
