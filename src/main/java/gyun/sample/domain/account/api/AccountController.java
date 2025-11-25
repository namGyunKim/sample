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
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Tag(name = "AccountController", description = "계정 관련 기능 뷰/API")
@Controller // @RestController -> @Controller로 변경
@RequestMapping(value = "/account")
@RequiredArgsConstructor
public class AccountController {

    // JWT 관련 유틸리티 제거
    // private final JwtTokenProvider jwtTokenProvider;

    private final WriteAccountService writeAccountService;
    private final LoginAccountValidator loginAccountValidator;

    // AOP에서 BindingResult를 처리하므로, 여기서는 @InitBinder만 남깁니다.
    @InitBinder("accountLoginRequest")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(loginAccountValidator);
    }

    /**
     * Spring Security가 기본 로그인 페이지 처리를 담당합니다.
     * GET 요청에 대해서만 뷰를 반환합니다.
     */
    @Operation(summary = "로그인 페이지 뷰")
    @GetMapping(value = "/login")
    public String loginForm(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {

        if (error != null) {
            model.addAttribute("errorMessage", "아이디 또는 비밀번호가 올바르지 않거나 비활성화된 계정입니다.");
        }
        if (logout != null) {
            model.addAttribute("logoutMessage", "로그아웃되었습니다.");
        }
        return "account/login"; // src/main/resources/templates/account/login.html
    }


    /**
     * 로그인 POST 요청 (Spring Security가 직접 처리)
     * - 이 메서드는 실제 Spring Security가 POST /login을 가로채서 처리하므로,
     * 유효성 검사 및 Validator 작동을 위해 @Deprecated 처리하고 로직은 비워둡니다.
     * Validator는 BindingAdvice AOP를 통해 여전히 작동됩니다.
     */
    @Operation(summary = "로그인 처리 (Spring Security로 대체됨)")
    @PostMapping(value = "/login")
    @Deprecated(since = "Spring Security Form Login", forRemoval = true)
    public String login(
            @Valid @ModelAttribute("accountLoginRequest") AccountLoginRequest accountLoginRequest,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        // 이 메서드는 Spring Security에 의해 무시되지만, Validator를 동작시키기 위해 존재합니다.
        // 유효성 검사 실패 시 BindingAdvice에서 BindingException이 발생하고,
        // ExceptionAdvice에서 이를 처리하려고 할 수 있으나, Form Login은 기본적으로
        // AuthenticationEntryPoint 대신 ExceptionTranslationFilter를 거치며,
        // AuthenticationFailureHandler가 실패를 처리합니다.
        // 현재 BindingAdvice의 예외 던지기 방식은 Rest API에 적합하므로,
        // Spring Security의 기본 로그인 성공/실패 핸들링을 활용하는 것이 좋습니다.
        // 폼 로그인에서는 Spring Security의 내부 동작을 믿고, 유효성 검사 실패 시에는
        // login.html에서 error 파라미터를 받아서 처리합니다.

        // 만약 Validator 오류가 발생하면, Spring Security는 이를 처리하지 못하고 500 에러를
        // 발생시키거나 기본 에러 페이지로 이동할 수 있습니다.
        // 따라서 LoginAccountValidator에서는 GlobalException 대신 Errors.reject()를 사용해야
        // Spring Security의 기본 인증 플로우를 따를 수 있습니다. (다만, 현재 코드가 Rest API 기반 예외처리를 하고 있으므로
        // LoginAccountValidator.java의 GlobalException throw 방식을 제거해야 합니다. -> 이 부분은 아래에서 수정합니다.)

        return "redirect:/"; // 성공 시 Index로 리다이렉트 (Spring Security의 defaultSuccessUrl과 일치해야 함)
    }

    @Operation(summary = "로그아웃 요청 (Spring Security가 처리)", description = "이 엔드포인트에 요청하면 Spring Security가 처리하고 /login?logout으로 리다이렉트합니다.")
    @GetMapping(value = "/logout")
    @PreAuthorize("isAuthenticated()")
    public String logout(
            @CurrentAccount CurrentAccountDTO currentAccountDTO,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes,
            Authentication authentication) {

        // 세션 기반에서는 Access Token을 사용하지 않으므로 null 전달
        writeAccountService.logout(currentAccountDTO, null);

        // 실제 로그아웃 처리는 Spring Security가 /logout POST 요청을 처리할 때 일어납니다.
        // 이 GET 요청은 단순한 링크 역할을 합니다.
        // 편의를 위해 여기에 비즈니스 로직(Refresh Token 무효화)을 두었습니다.
        redirectAttributes.addFlashAttribute("logoutMessage", "정상적으로 로그아웃되었습니다.");
        return "redirect:/logout"; // Spring Security의 /logout 엔드포인트를 호출
    }


    @Operation(summary = "로그인한 데이터 (뷰)", description = "로그인한 사용자의 정보를 표시하는 뷰")
    @GetMapping(value = "/profile")
    @PreAuthorize("isAuthenticated()")
    public String loginData(@CurrentAccount CurrentAccountDTO request, Model model) {
        LoginMemberResponse response = writeAccountService.getLoginData(request);
        model.addAttribute("member", response);
        return "account/profile";
    }

    // JWT 에러, 권한 에러 관련 엔드포인트 제거 (Spring Security가 처리)
    @Hidden
    @RequestMapping(value = "/jwt-error/{errorCode}")
    public void jwtError(@PathVariable String errorCode) {
        // 세션 기반에서 사용하지 않으므로 제거하거나 @Deprecated 처리
        throw new UnsupportedOperationException("세션 기반 프로젝트에서는 JWT 에러 컨트롤러를 사용하지 않습니다.");
    }

    @Hidden
    @RequestMapping(value = "/access-denied/{errorMessage}")
    public void accessError(@PathVariable String errorMessage) {
        // 세션 기반에서 사용하지 않으므로 제거하거나 @Deprecated 처리
        throw new UnsupportedOperationException("세션 기반 프로젝트에서는 권한 에러 컨트롤러를 사용하지 않습니다.");
    }
}