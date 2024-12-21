package gyun.sample.domain.account.api;

import gyun.sample.domain.account.payload.dto.CurrentAccountDTO;
import gyun.sample.domain.account.payload.response.LoginMemberResponse;
import gyun.sample.domain.account.service.WriteAccountService;
import gyun.sample.domain.account.validator.LoginAccountValidator;
import gyun.sample.global.annotaion.CurrentAccount;
import gyun.sample.global.api.RestApiController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AccountController", description = "계정 관련 기능 api")
@RestController
@RequestMapping(value = "/api/account")
@RequiredArgsConstructor
public class AccountController {
    //    utils
    private final RestApiController restApiController;
    //    service
    private final WriteAccountService writeAccountService;

    private final LoginAccountValidator loginAccountValidator;

    @InitBinder("accountLoginRequest")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(loginAccountValidator);
    }


    @Operation(summary = "로그인한 데이터")
    @GetMapping(value = "/get-login-data")
    public ResponseEntity<String> loginData(@CurrentAccount CurrentAccountDTO request) {
        LoginMemberResponse response = writeAccountService.getLoginData(request);
        return restApiController.createSuccessRestResponse(response);
    }

}
