package gyun.sample.domain.account.api;

import gyun.sample.domain.account.dto.CurrentAccountDTO;
import gyun.sample.domain.account.payload.request.AccountLoginRequest;
import gyun.sample.domain.account.payload.response.AccountLoginResponse;
import gyun.sample.domain.account.service.AccountService;
import gyun.sample.global.annotaion.CurrentAccount;
import gyun.sample.global.api.RestApiController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO: 2023/09/04 JWT 기능 고도화 필요 리프레쉬로 토큰 재발급 기능 및 로그아웃 미구현 로그인 기능이 재발급과 겹치는부분이 많아서 정리 필요
@Tag(name = "AccountController", description = "계정 관련 기능 api")
@RestController
@RequestMapping(value = "/api/account")
//@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class AccountController {
    private final RestApiController restApiController;
    private final AccountService accountService;

    @Operation(summary = "로그인")
    @PostMapping(value = "/login")
    public ResponseEntity<String> login(@Valid @RequestBody AccountLoginRequest request,
                                        BindingResult bindingResult) {
        AccountLoginResponse response = accountService.login(request);
        return restApiController.createRestResponse(response);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "로그아웃")
    @PostMapping(value = "/logout")
    public ResponseEntity<String> logout(@CurrentAccount CurrentAccountDTO account) {
        return restApiController.createSuccessRestResponse(true);
    }

    @Operation(summary = "리프레쉬 토큰으로 JWT 토큰 재발급")
    @PostMapping(value = "/get-token-by-refresh")
    public ResponseEntity<String> getAccessToken(@RequestBody String refreshToken) {
        AccountLoginResponse response = accountService.getJwtTokenByRefreshToken(refreshToken);
        return restApiController.createSuccessRestResponse(response);
    }

}
