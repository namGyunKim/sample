package gyun.sample.domain.account.api;

import gyun.sample.domain.account.payload.dto.CurrentAccountDTO;
import gyun.sample.domain.account.payload.response.LoginMemberResponse;
import gyun.sample.domain.account.service.WriteAccountService;
import gyun.sample.global.annotaion.CurrentAccount;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Tag(name = "AccountController", description = "계정 관련 뷰 컨트롤러")
@Controller
@RequestMapping(value = "/account")
@RequiredArgsConstructor
public class AccountController {

    private final WriteAccountService writeAccountService;

    /**
     * 로그인 페이지
     * 실제 인증은 Spring Security (POST /login)가 처리합니다.
     */
    @Operation(summary = "로그인 페이지")
    @GetMapping(value = "/login")
    public String loginForm(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {

        if (error != null) {
            model.addAttribute("errorMessage", "아이디 또는 비밀번호가 일치하지 않거나, 비활성화된 계정입니다.");
        }
        if (logout != null) {
            model.addAttribute("logoutMessage", "성공적으로 로그아웃되었습니다.");
        }
        return "account/login";
    }

    /**
     * 내 프로필 페이지
     * @CurrentAccount를 통해 현재 로그인한 사용자 정보를 주입받습니다.
     */
    @Operation(summary = "내 프로필 페이지")
    @GetMapping(value = "/profile")
    @PreAuthorize("isAuthenticated()")
    public String profile(@CurrentAccount CurrentAccountDTO currentAccount, Model model) {
        LoginMemberResponse response = writeAccountService.getLoginData(currentAccount);
        model.addAttribute("member", response);
        return "account/profile";
    }
}