package gyun.sample.domain.account.api;

import gyun.sample.domain.account.payload.dto.CurrentAccountDTO;
import gyun.sample.domain.account.payload.request.AccountLoginRequest;
import gyun.sample.domain.account.payload.response.LoginMemberResponse;
import gyun.sample.domain.account.service.WriteAccountService;
import gyun.sample.domain.account.validator.LoginAccountValidator;
import gyun.sample.global.annotaion.CurrentAccount;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Tag(name = "AccountController", description = "계정 관련 기능 뷰/API")
@Controller
@RequestMapping(value = "/account")
@RequiredArgsConstructor
public class AccountController {

    private final WriteAccountService writeAccountService;
    private final LoginAccountValidator loginAccountValidator;

    // AOP에서 BindingResult를 처리하므로, 여기서는 @InitBinder만 남깁니다.
    // Spring Security 폼 로그인을 사용할 경우, 이 Binder는 Controller 메서드가 호출될 때만 작동합니다.
    @InitBinder("accountLoginRequest")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(loginAccountValidator);
    }

    /**
     * 로그인 페이지 뷰
     */
    @Operation(summary = "로그인 페이지 뷰")
    @GetMapping(value = "/login")
    public String loginForm(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {

        if (error != null) {
            // Spring Security 인증 실패 시 error 파라미터가 전달됨
            model.addAttribute("errorMessage", "아이디 또는 비밀번호가 올바르지 않거나 비활성화된 계정입니다.");
        }
        if (logout != null) {
            model.addAttribute("logoutMessage", "로그아웃되었습니다.");
        }
        return "account/login";
    }

    /**
     * 로그인 처리 (POST)
     * - 실제 처리는 Spring Security(UsernamePasswordAuthenticationFilter)가 가로채서 수행합니다.
     * - 따라서 이 메서드 본문은 실행되지 않으나, 문서화 및 구조 유지를 위해 남겨둡니다.
     * - 유효성 검사는 Security Config의 UserDetailsService(PrincipalDetailsService)에서 수행됩니다.
     */
    @Operation(summary = "로그인 처리 (Spring Security 위임)")
    @PostMapping(value = "/login")
    public String login(
            @Valid @ModelAttribute("accountLoginRequest") AccountLoginRequest accountLoginRequest,
            BindingResult bindingResult) {
        // Spring Security가 요청을 가로채므로 이 코드는 도달하지 않습니다.
        return "redirect:/";
    }

    @Operation(summary = "로그아웃 요청")
    @GetMapping(value = "/logout")
    @PreAuthorize("isAuthenticated()")
    public String logout(
            @CurrentAccount CurrentAccountDTO currentAccountDTO,
            RedirectAttributes redirectAttributes) {

        // DB Refresh Token 삭제 등 비즈니스 로직 수행
        writeAccountService.logout(currentAccountDTO, null);

        redirectAttributes.addFlashAttribute("logoutMessage", "정상적으로 로그아웃되었습니다.");
        return "redirect:/logout"; // Security의 로그아웃 핸들러로 리다이렉트
    }

    @Operation(summary = "로그인한 데이터 (뷰)")
    @GetMapping(value = "/profile")
    @PreAuthorize("isAuthenticated()")
    public String loginData(@CurrentAccount CurrentAccountDTO currentAccountDTO, Model model) {
        LoginMemberResponse response = writeAccountService.getLoginData(currentAccountDTO);
        model.addAttribute("member", response);
        return "account/profile";
    }

    @Hidden
    @RequestMapping(value = "/jwt-error/{errorCode}")
    public void jwtError(@PathVariable String errorCode) {
        throw new UnsupportedOperationException("Not supported in Session based project");
    }

    @Hidden
    @RequestMapping(value = "/access-denied/{errorMessage}")
    public void accessError(@PathVariable String errorMessage) {
        throw new UnsupportedOperationException("Not supported in Session based project");
    }
}