package gyun.sample.domain.account.api;

import gyun.sample.domain.account.payload.request.AccountLoginRequest;
import gyun.sample.domain.account.payload.request.AccountLogoutRequest;
import gyun.sample.domain.account.payload.response.AccountLoginResponse;
import gyun.sample.domain.account.service.AccountService;
import gyun.sample.global.api.RestApiController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AccountController", description = "계정 관련 기능 api")
@RestController
@RequestMapping(value = "/api/account")
//@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class AccountController {
    //    utils
    private final RestApiController restApiController;
    //    service
    private final AccountService accountService;


    @Operation(summary = "JWT 에러")
    @GetMapping(value = "/jwt-error")
    public void jwtError() {
     accountService.jwtErrorException();
    }

    @Operation(summary = "로그인")
    @PostMapping(value = "/login")
    public ResponseEntity<String> login(@Valid @RequestBody AccountLoginRequest request,
                                        BindingResult bindingResult) {
        AccountLoginResponse response = accountService.login(request);
        return restApiController.createRestResponse(response);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "로그아웃(Refresh Token Delete)")
    @PostMapping(value = "/logout")
    public ResponseEntity<String> logout(@Valid @RequestBody AccountLogoutRequest request,
                                         BindingResult bindingResult) {

        boolean response = accountService.logout(request);
        return restApiController.createSuccessRestResponse(response);
    }

    @Operation(summary = "리프레쉬 토큰으로 JWT 토큰 재발급")
    @PostMapping(value = "/get-token-by-refresh")
    public ResponseEntity<String> getAccessToken(@RequestBody String refreshToken) {
        AccountLoginResponse response = accountService.getJwtTokenByRefreshToken(refreshToken);
        return restApiController.createSuccessRestResponse(response);
    }

}
