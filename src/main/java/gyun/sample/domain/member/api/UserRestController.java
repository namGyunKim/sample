package gyun.sample.domain.member.api;


import gyun.sample.domain.account.payload.dto.CurrentAccountDTO;
import gyun.sample.domain.member.payload.dto.MemberListRequestDTO;
import gyun.sample.domain.member.payload.request.MemberUpdateRequest;
import gyun.sample.domain.member.payload.request.MemberUserCreateRequest;
import gyun.sample.domain.member.payload.request.MemberUserListRequest;
import gyun.sample.domain.member.service.read.ReadMemberService;
import gyun.sample.domain.member.service.write.WriteMemberService;
import gyun.sample.domain.member.validator.MemberUserCreateValidator;
import gyun.sample.domain.member.validator.MemberUserListValidator;
import gyun.sample.domain.member.validator.MemberUserUpdateValidator;
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
    private final WriteMemberService<MemberUserCreateRequest> writeUserService;
    private final ReadMemberService readUserService;
    private final MemberUserCreateValidator memberUserCreateValidator;
    private final MemberUserListValidator memberUserListValidator;
    private final MemberUserUpdateValidator memberUserUpdateValidator;


    @InitBinder(value = "memberUserCreateRequest")
    public void initBinder(WebDataBinder dataBinder) {
        dataBinder.addValidators(memberUserCreateValidator);
    }

    @InitBinder(value = "memberUserListRequest")
    public void initBinder2(WebDataBinder dataBinder) {
        dataBinder.addValidators(memberUserListValidator);
    }

    @InitBinder(value = "memberUpdateRequest")
    public void initBinder3(WebDataBinder dataBinder) {
        dataBinder.addValidators(memberUserUpdateValidator);
    }

    @Operation(summary = "유저 생성")
    @PostMapping(value = "/create")
    public ResponseEntity<String> createUser(@Valid @RequestBody MemberUserCreateRequest memberUserCreateRequest, BindingResult bindingResult) {

        GlobalCreateResponse response = writeUserService.createMember(memberUserCreateRequest);
        return restApiController.createRestResponse(response);
    }

    @Operation(summary = "유저 목록")
    @GetMapping(value = "/list")
    public ResponseEntity<String> getUserList(@Valid MemberUserListRequest memberUserListRequest, BindingResult bindingResult) {
        MemberListRequestDTO memberListRequestDTO = new MemberListRequestDTO(memberUserListRequest);
        return restApiController.createRestResponse(readUserService.getList(memberListRequestDTO));
    }

    @Operation(summary = "유저 상세")
    @GetMapping(value = "/detail/{id}")
    public ResponseEntity<String> getUserDetail(@PathVariable long id) {
        return restApiController.createRestResponse(readUserService.getDetail(id));
    }

    @Operation(summary = "유저 수정")
    @PutMapping(value = "/update")
    public ResponseEntity<String> updateUser(@Valid @RequestBody MemberUpdateRequest memberUpdateRequest, BindingResult bindingResult,
                                             @CurrentAccount CurrentAccountDTO currentAccountDTO) {
        GlobalUpdateResponse response = writeUserService.updateMember(memberUpdateRequest, currentAccountDTO.loginId());
        return restApiController.createRestResponse(response);
    }

    @Operation(summary = "유저 비활성화")
    @PatchMapping(value = "/inactive")
    public ResponseEntity<String> inactiveUser(@CurrentAccount CurrentAccountDTO currentAccountDTO) {
        GlobalInactiveResponse response = writeUserService.deActiveMember(currentAccountDTO.loginId());
        return restApiController.createRestResponse(response);
    }
}
