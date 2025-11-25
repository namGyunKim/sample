package gyun.sample.domain.account.api;

import gyun.sample.domain.account.payload.dto.CurrentAccountDTO;
import gyun.sample.domain.account.payload.request.AccountLoginRequest;
import gyun.sample.domain.account.payload.response.AccountLoginResponse;
import gyun.sample.domain.account.payload.response.LoginMemberResponse;
import gyun.sample.domain.account.service.WriteAccountService;
import gyun.sample.domain.account.validator.LoginAccountValidator;
import gyun.sample.global.annotaion.CurrentAccount;
import gyun.sample.global.api.RestApiController;
import gyun.sample.global.payload.response.RestApiResponse;
import gyun.sample.global.utils.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AccountController", description = "계정 관련 기능 api")
@RestController
@RequestMapping(value = "/api/account")
@RequiredArgsConstructor
public class AccountController {
    //    utils
    private final RestApiController restApiController;
    private final JwtTokenProvider jwtTokenProvider;
    //    service
    private final WriteAccountService writeAccountService;

    private final LoginAccountValidator loginAccountValidator;

    @InitBinder("accountLoginRequest")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(loginAccountValidator);
    }

    @Hidden
    @Operation(summary = "JWT 에러")
    @RequestMapping(value = "/jwt-error/{errorCode}")
    public void jwtError(@PathVariable String errorCode) {
        writeAccountService.jwtErrorException(errorCode);
    }

    @Hidden
    @Operation(summary = "권한 에러")
    @RequestMapping(value = "/access-denied/{errorMessage}")
    public void accessError(@PathVariable String errorMessage) {
        writeAccountService.AccessException(errorMessage);
    }

    @Operation(summary = "로그인")
    @PostMapping(value = "/login")
    // SecurityConfig에서 WhiteList로 이미 열려있지만 명시적으로 permitAll 가능
    public ResponseEntity<RestApiResponse<AccountLoginResponse>> login(
            @Valid @RequestBody AccountLoginRequest accountLoginRequest,
            BindingResult bindingResult) {
        AccountLoginResponse response = writeAccountService.login(accountLoginRequest);
        return restApiController.createRestResponse(response);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "로그아웃(Access/Refresh Token 무효화)")
    @PostMapping(value = "/logout")
    @PreAuthorize("isAuthenticated()")
    // HttpServletRequest를 받아 Access Token을 추출하여 블랙리스트에 추가합니다.
    public ResponseEntity<RestApiResponse<Boolean>> logout(
            @CurrentAccount CurrentAccountDTO currentAccountDTO,
            HttpServletRequest request) {

        String accessToken = jwtTokenProvider.resolveToken(request);
        boolean response = writeAccountService.logout(currentAccountDTO, accessToken);

        return restApiController.createSuccessRestResponse(response);
    }

    @Operation(summary = "리프레쉬 토큰으로 JWT 토큰 재발급")
    @PostMapping(value = "/get-token-by-refresh/{refreshToken}")
    public ResponseEntity<RestApiResponse<AccountLoginResponse>> getAccessToken(@PathVariable String refreshToken) {
        AccountLoginResponse response = writeAccountService.getJwtTokenByRefreshToken(refreshToken);
        return restApiController.createSuccessRestResponse(response);
    }

    @Operation(summary = "로그인한 데이터")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping(value = "/get-login-data")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RestApiResponse<LoginMemberResponse>> loginData(@CurrentAccount CurrentAccountDTO request) {
        LoginMemberResponse response = writeAccountService.getLoginData(request);
        return restApiController.createSuccessRestResponse(response);
    }
}