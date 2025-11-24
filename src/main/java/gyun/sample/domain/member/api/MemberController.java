package gyun.sample.domain.member.api;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.account.payload.dto.CurrentAccountDTO;
import gyun.sample.domain.member.payload.dto.MemberListRequestDTO;
import gyun.sample.domain.member.payload.request.MemberCreateRequest;
import gyun.sample.domain.member.payload.request.MemberListRequest;
import gyun.sample.domain.member.payload.request.MemberUpdateRequest;
import gyun.sample.domain.member.service.MemberStrategyFactory;
import gyun.sample.domain.member.service.read.ReadMemberService;
import gyun.sample.domain.member.service.write.WriteMemberService;
import gyun.sample.domain.member.validator.MemberCreateValidator;
import gyun.sample.domain.member.validator.MemberListValidator;
import gyun.sample.domain.member.validator.MemberUserUpdateValidator;
import gyun.sample.global.annotaion.CurrentAccount;
import gyun.sample.global.api.RestApiController;
import gyun.sample.global.payload.response.GlobalCreateResponse;
import gyun.sample.global.payload.response.GlobalInactiveResponse;
import gyun.sample.global.payload.response.GlobalUpdateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "MemberController", description = "통합 회원(유저/관리자) API")
@RestController
@RequestMapping(value = "/api/member/{role}")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class MemberController {

    private final RestApiController restApiController;
    private final MemberStrategyFactory memberStrategyFactory;

    // 통합 Validators
    private final MemberCreateValidator memberCreateValidator;
    private final MemberListValidator memberListValidator;
    private final MemberUserUpdateValidator memberUserUpdateValidator;

    @InitBinder("memberCreateRequest")
    public void initBinderCreate(WebDataBinder dataBinder) {
        dataBinder.addValidators(memberCreateValidator);
    }

    @InitBinder("memberListRequest")
    public void initBinderList(WebDataBinder dataBinder) {
        dataBinder.addValidators(memberListValidator);
    }

    @InitBinder("memberUpdateRequest")
    public void initBinderUpdate(WebDataBinder dataBinder) {
        dataBinder.addValidators(memberUserUpdateValidator);
    }

    @Operation(summary = "회원 생성")
    @PostMapping(value = "/create")
    public ResponseEntity<String> createMember(
            @Parameter(description = "Account Role (USER, ADMIN, SUPER_ADMIN)", example = "USER")
            @PathVariable AccountRole role,
            @Valid @RequestBody MemberCreateRequest memberCreateRequest,
            BindingResult bindingResult) {

        WriteMemberService service = memberStrategyFactory.getWriteService(role);
        GlobalCreateResponse response = service.createMember(memberCreateRequest);
        return restApiController.createSuccessRestResponse(response);
    }

    @Operation(summary = "회원 목록 조회")
    @GetMapping(value = "/list")
    public ResponseEntity<String> getMemberList(
            @PathVariable AccountRole role,
            @Valid MemberListRequest memberListRequest,
            BindingResult bindingResult) {

        ReadMemberService service = memberStrategyFactory.getReadService(role);
        // Service Layer DTO로 변환
        MemberListRequestDTO listRequestDTO = new MemberListRequestDTO(memberListRequest);
        return restApiController.createRestResponse(service.getList(listRequestDTO));
    }

    @Operation(summary = "회원 상세 조회")
    @GetMapping(value = "/detail/{id}")
    public ResponseEntity<String> getMemberDetail(
            @PathVariable AccountRole role,
            @PathVariable long id) {

        ReadMemberService service = memberStrategyFactory.getReadService(role);
        return restApiController.createRestResponse(service.getDetail(id));
    }

    @Operation(summary = "회원 정보 수정")
    @PutMapping(value = "/update")
    public ResponseEntity<String> updateMember(
            @PathVariable AccountRole role,
            @Valid @RequestBody MemberUpdateRequest memberUpdateRequest,
            BindingResult bindingResult,
            @CurrentAccount CurrentAccountDTO currentAccountDTO) {

        WriteMemberService service = memberStrategyFactory.getWriteService(role);
        GlobalUpdateResponse response = service.updateMember(memberUpdateRequest, currentAccountDTO.loginId());
        return restApiController.createRestResponse(response);
    }

    @Operation(summary = "회원 비활성화")
    @PatchMapping(value = "/inactive")
    public ResponseEntity<String> inactiveMember(
            @PathVariable AccountRole role,
            @CurrentAccount CurrentAccountDTO currentAccountDTO) {

        WriteMemberService service = memberStrategyFactory.getWriteService(role);
        GlobalInactiveResponse response = service.deActiveMember(currentAccountDTO.loginId());
        return restApiController.createRestResponse(response);
    }
}