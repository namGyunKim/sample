package gyun.sample.domain.member.api;


import gyun.sample.domain.member.payload.request.admin.CreateMemberRequest;
import gyun.sample.domain.member.payload.request.admin.GetMemberListRequest;
import gyun.sample.domain.member.service.read.ReadMemberService;
import gyun.sample.domain.member.service.write.WriteMemberService;
import gyun.sample.domain.member.validator.CreateAdminValidator;
import gyun.sample.domain.member.validator.GetAdminListValidator;
import gyun.sample.global.api.RestApiController;
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

    private final RestApiController restApiController;
    private final WriteMemberService writeAdminService;
    private final ReadMemberService readAdminService;
    private final CreateAdminValidator createAdminValidator;
    private final GetAdminListValidator getAdminListValidator;



    @InitBinder(value = "createMemberRequest")
    public void initBinder(WebDataBinder dataBinder) {
        dataBinder.addValidators(createAdminValidator);
    }

    @InitBinder(value = "getMemberListRequest")
    public void initBinder2(WebDataBinder dataBinder) {
        dataBinder.addValidators(getAdminListValidator);
    }

    @Operation(summary = "관리자 생성")
    @PostMapping(value = "/create")
    public ResponseEntity<String> createAdmin(@Valid @RequestBody CreateMemberRequest createMemberRequest, BindingResult bindingResult) {
        GlobalCreateResponse response = writeAdminService.createMember(createMemberRequest);
        return restApiController.createRestResponse(response);
    }

    @Operation(summary = "관리자 목록")
    @GetMapping(value = "/list")
    public ResponseEntity<String> getAdminList(@Valid GetMemberListRequest getMemberListRequest,BindingResult bindingResult) {
        return restApiController.createRestResponse(readAdminService.getList(getMemberListRequest));
    }

    @Operation(summary = "관리자 상세")
    @GetMapping(value = "/detail/{id}")
    public ResponseEntity<String> getAdminDetail(@PathVariable long id) {
        return restApiController.createRestResponse(readAdminService.getDetail(id));
    }
}
