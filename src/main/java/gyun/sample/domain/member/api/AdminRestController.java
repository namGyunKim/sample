package gyun.sample.domain.member.api;


import gyun.sample.domain.account.dto.CurrentAccountDTO;
import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.payload.request.admin.CreateMemberRequest;
import gyun.sample.domain.member.service.write.WriteMemberService;
import gyun.sample.domain.member.validator.CreateAdminValidator;
import gyun.sample.global.annotaion.CurrentAccount;
import gyun.sample.global.api.RestApiController;
import gyun.sample.global.error.enums.ErrorCode;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.payload.response.GlobalCreateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AdminRestController", description = "관리자 api")
@RestController
@RequestMapping(value = "/api/admin", headers = "X-API-VERSION=1")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class AdminRestController {

    //    utils
    private final RestApiController restApiController;
    private final WriteMemberService writeAdminService;

    //    validator
    private final CreateAdminValidator createAdminValidator;


    @InitBinder(value = "createMemberRequest")
    public void initBinder(WebDataBinder dataBinder) {
        dataBinder.addValidators(createAdminValidator);
    }

    @Operation(summary = "관리자 생성")
    @PostMapping(value = "/create")
    public ResponseEntity<String> createAdmin(@Valid @RequestBody CreateMemberRequest createMemberRequest, BindingResult bindingResult,
                                              @CurrentAccount CurrentAccountDTO currentAccountDTO) {
        if (!currentAccountDTO.role().equals(AccountRole.SUPER_ADMIN)) {
            throw new GlobalException(ErrorCode.ACCESS_DENIED, "최고 관리자만 접근 가능합니다.");
        }
        GlobalCreateResponse response = writeAdminService.createMember(createMemberRequest);
        return restApiController.createRestResponse(response);
    }

}
