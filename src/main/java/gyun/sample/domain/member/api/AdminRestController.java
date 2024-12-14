package gyun.sample.domain.member.api;


import gyun.sample.domain.account.payload.dto.CurrentAccountDTO;
import gyun.sample.domain.member.payload.request.AllMemberRequest;
import gyun.sample.domain.member.payload.request.CreateMemberAdminRequest;
import gyun.sample.domain.member.payload.request.UpdateMemberRequest;
import gyun.sample.domain.member.service.read.ReadMemberService;
import gyun.sample.domain.member.service.write.WriteMemberService;
import gyun.sample.domain.member.validator.AllAdminValidator;
import gyun.sample.domain.member.validator.CreateAdminValidator;
import gyun.sample.domain.member.validator.UpdateAdminValidator;
import gyun.sample.global.annotaion.CurrentAccount;
import gyun.sample.global.api.RestApiController;
import gyun.sample.global.payload.response.GlobalCreateResponse;
import gyun.sample.global.payload.response.GlobalInactiveResponse;
import gyun.sample.global.payload.response.GlobalUpdateResponse;
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
@RequestMapping(value = "/api/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class AdminRestController {

    private final RestApiController restApiController;
    private final WriteMemberService<CreateMemberAdminRequest> writeAdminService;
    private final ReadMemberService readAdminService;
    private final CreateAdminValidator createAdminValidator;
    private final AllAdminValidator allAdminValidator;
    private final UpdateAdminValidator updateAdminValidator;


    @InitBinder(value = "createMemberAdminRequest")
    public void initBinder(WebDataBinder dataBinder) {
        dataBinder.addValidators(createAdminValidator);
    }

    @InitBinder(value = "allMemberRequest")
    public void initBinder2(WebDataBinder dataBinder) {
        dataBinder.addValidators(allAdminValidator);
    }

    @InitBinder(value = "updateMemberRequest")
    public void initBinder3(WebDataBinder dataBinder) {
        dataBinder.addValidators(updateAdminValidator);
    }

    @Operation(summary = "관리자 생성")
    @PostMapping(value = "/create")
    public ResponseEntity<String> createAdmin(@Valid @RequestBody CreateMemberAdminRequest createMemberAdminRequest, BindingResult bindingResult) {
        GlobalCreateResponse response = writeAdminService.createMember(createMemberAdminRequest);
        return restApiController.createRestResponse(response);
    }

    @Operation(summary = "관리자 목록")
    @GetMapping(value = "/list")
    public ResponseEntity<String> getAdminList(@Valid AllMemberRequest allMemberRequest, BindingResult bindingResult) {
        return restApiController.createRestResponse(readAdminService.getList(allMemberRequest));
    }

    @Operation(summary = "관리자 상세")
    @GetMapping(value = "/detail/{id}")
    public ResponseEntity<String> getAdminDetail(@PathVariable long id) {
        return restApiController.createRestResponse(readAdminService.getDetail(id));
    }

    @Operation(summary = "관리자 수정")
    @PutMapping(value = "/update")
    public ResponseEntity<String> updateAdmin(@Valid @RequestBody UpdateMemberRequest updateMemberRequest, BindingResult bindingResult,
                                              @CurrentAccount CurrentAccountDTO currentAccountDTO) {
        GlobalUpdateResponse response = writeAdminService.updateMember(updateMemberRequest, currentAccountDTO.loginId());
        return restApiController.createRestResponse(response);
    }

    @Operation(summary = "관리자 비활성화")
    @PatchMapping(value = "/inactive")
    public ResponseEntity<String> inactiveAdmin(@CurrentAccount CurrentAccountDTO currentAccountDTO) {
        GlobalInactiveResponse response = writeAdminService.deActiveMember(currentAccountDTO.loginId());
        return restApiController.createRestResponse(response);
    }
}
