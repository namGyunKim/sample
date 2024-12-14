package gyun.sample.domain.member.api;


import gyun.sample.domain.account.payload.dto.CurrentAccountDTO;
import gyun.sample.domain.member.payload.request.AllMemberRequest;
import gyun.sample.domain.member.payload.request.CreateMemberUserRequest;
import gyun.sample.domain.member.payload.request.UpdateMemberRequest;
import gyun.sample.domain.member.service.read.ReadMemberService;
import gyun.sample.domain.member.service.write.WriteMemberService;
import gyun.sample.domain.member.validator.AllUserValidator;
import gyun.sample.domain.member.validator.CreateUserValidator;
import gyun.sample.domain.member.validator.UpdateUserValidator;
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

@Tag(name = "UserRestController", description = "유저 api")
@RestController
@RequestMapping(value = "/api/user")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class UserRestController {

    private final RestApiController restApiController;
    private final WriteMemberService<CreateMemberUserRequest> writeUserService;
    private final ReadMemberService readUserService;
    private final CreateUserValidator createUserValidator;
    private final AllUserValidator allUserValidator;
    private final UpdateUserValidator updateUserValidator;


    @InitBinder(value = "createMemberUserRequest")
    public void initBinder(WebDataBinder dataBinder) {
        dataBinder.addValidators(createUserValidator);
    }

    @InitBinder(value = "allMemberRequest")
    public void initBinder2(WebDataBinder dataBinder) {
        dataBinder.addValidators(allUserValidator);
    }

    @InitBinder(value = "updateMemberRequest")
    public void initBinder3(WebDataBinder dataBinder) {
        dataBinder.addValidators(updateUserValidator);
    }

    @Operation(summary = "유저 생성")
    @PostMapping(value = "/create")
    public ResponseEntity<String> createUser(@Valid @RequestBody CreateMemberUserRequest createMemberUserRequest, BindingResult bindingResult) {

        GlobalCreateResponse response = writeUserService.createMember(createMemberUserRequest);
        return restApiController.createRestResponse(response);
    }

    @Operation(summary = "유저 목록")
    @GetMapping(value = "/list")
    public ResponseEntity<String> getUserList(@Valid AllMemberRequest allMemberRequest, BindingResult bindingResult) {
        return restApiController.createRestResponse(readUserService.getList(allMemberRequest));
    }

    @Operation(summary = "유저 상세")
    @GetMapping(value = "/detail/{id}")
    public ResponseEntity<String> getUserDetail(@PathVariable long id) {
        return restApiController.createRestResponse(readUserService.getDetail(id));
    }

    @Operation(summary = "유저 수정")
    @PutMapping(value = "/update")
    public ResponseEntity<String> updateUser(@Valid @RequestBody UpdateMemberRequest updateMemberRequest, BindingResult bindingResult,
                                             @CurrentAccount CurrentAccountDTO currentAccountDTO) {
        GlobalUpdateResponse response = writeUserService.updateMember(updateMemberRequest, currentAccountDTO.loginId());
        return restApiController.createRestResponse(response);
    }

    @Operation(summary = "유저 비활성화")
    @PatchMapping(value = "/inactive")
    public ResponseEntity<String> inactiveUser(@CurrentAccount CurrentAccountDTO currentAccountDTO) {
        GlobalInactiveResponse response = writeUserService.deActiveMember(currentAccountDTO.loginId());
        return restApiController.createRestResponse(response);
    }
}
