package gyun.sample.domain.account.api;

import gyun.sample.domain.account.dto.CurrentAccountDTO;
import gyun.sample.domain.account.payload.request.AccountLoginRequest;
import gyun.sample.domain.account.payload.response.AccountLoginResponse;
import gyun.sample.domain.account.payload.response.FindLoginMemberResponse;
import gyun.sample.domain.account.service.ReadAccountService;
import gyun.sample.domain.account.service.WriteAccountService;
import gyun.sample.global.annotaion.CurrentAccount;
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
@RequestMapping(value = "/api/account",headers = "X-API-VERSION=1")
@RequiredArgsConstructor
public class AccountController {
    //    utils
    private final RestApiController restApiController;
    //    service
    private final WriteAccountService writeAccountService;
    private final ReadAccountService readAccountService;
    @Operation(summary = "JWT 에러")
    @GetMapping(value = "/jwt-error")
    public void jwtError() {
     writeAccountService.jwtErrorException();
    }

    @Operation(summary = "로그인")
    @PostMapping(value = "/login")
    public ResponseEntity<String> login(@Valid @RequestBody AccountLoginRequest request,
                                        BindingResult bindingResult) {
        AccountLoginResponse response = writeAccountService.login(request);
        return restApiController.createRestResponse(response);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "로그아웃(Refresh Token Delete)")
    @GetMapping(value = "/logout/{refreshToken}")
    public ResponseEntity<String> logout(@PathVariable String refreshToken) {

        boolean response = writeAccountService.logout(refreshToken);
        return restApiController.createSuccessRestResponse(response);
    }

    @Operation(summary = "리프레쉬 토큰으로 JWT 토큰 재발급")
    @GetMapping(value = "/get-token-by-refresh/{refreshToken}")
    public ResponseEntity<String> getAccessToken(@PathVariable String refreshToken) {
        AccountLoginResponse response = writeAccountService.getJwtTokenByRefreshToken(refreshToken);
        return restApiController.createSuccessRestResponse(response);
    }

    @Operation(summary = "로그인한 데이터")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping(value = "/login-data")
    public ResponseEntity<String> loginData(@CurrentAccount CurrentAccountDTO request) {
        FindLoginMemberResponse response = readAccountService.findLoginData(request);
        return restApiController.createSuccessRestResponse(response);
    }

}
