package gyun.sample.domain.member.api;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.account.payload.dto.CurrentAccountDTO;
import gyun.sample.domain.member.payload.dto.MemberListRequestDTO;
import gyun.sample.domain.member.payload.request.MemberCreateRequest;
import gyun.sample.domain.member.payload.request.MemberListRequest;
import gyun.sample.domain.member.payload.request.MemberUpdateRequest;
import gyun.sample.domain.member.payload.response.DetailMemberResponse;
import gyun.sample.domain.member.payload.response.MemberListResponse;
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
import gyun.sample.global.payload.response.RestApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    // [중요] @InitBinder의 value("이름")는 컨트롤러 메서드의 파라미터 변수명(또는 @ModelAttribute 이름)과
    // 반드시 정확히 일치해야만 해당 객체 검증 시 Validator가 동작합니다.
    // 예: @InitBinder("memberCreateRequest") -> createMember(@RequestBody ... memberCreateRequest)

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
    // 관리자 생성은 관리자만, 유저 생성은 누구나(또는 상황에 따라 조정)
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or #role == T(gyun.sample.domain.account.enums.AccountRole).USER")
    public ResponseEntity<RestApiResponse<GlobalCreateResponse>> createMember(
            @Parameter(description = "Account Role", example = "USER") @PathVariable AccountRole role,
            // @InitBinder("memberCreateRequest")와 변수명 일치 필수
            @Valid @RequestBody MemberCreateRequest memberCreateRequest,
            BindingResult bindingResult) {

        WriteMemberService service = memberStrategyFactory.getWriteService(role);
        GlobalCreateResponse response = service.createMember(memberCreateRequest);
        return restApiController.createSuccessRestResponse(response);
    }

    @Operation(summary = "회원 목록 조회")
    @GetMapping(value = "/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<RestApiResponse<Page<MemberListResponse>>> getMemberList(
            @PathVariable AccountRole role,
            // @InitBinder("memberListRequest")와 변수명 일치 필수
            @Valid MemberListRequest memberListRequest,
            BindingResult bindingResult) {

        ReadMemberService service = memberStrategyFactory.getReadService(role);
        var listRequestDTO = new MemberListRequestDTO(memberListRequest);
        return restApiController.createRestResponse(service.getList(listRequestDTO));
    }

    @Operation(summary = "회원 상세 조회")
    @GetMapping(value = "/detail/{id}")
    @PreAuthorize("isAuthenticated()") // 본인 조회 로직 or 관리자 조회 로직은 서비스 내부 또는 더 상세한 SpEL로 처리 가능
    public ResponseEntity<RestApiResponse<DetailMemberResponse>> getMemberDetail(
            @PathVariable AccountRole role,
            @PathVariable long id) {

        ReadMemberService service = memberStrategyFactory.getReadService(role);
        return restApiController.createRestResponse(service.getDetail(id));
    }

    @Operation(summary = "회원 정보 수정")
    @PutMapping(value = "/update")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RestApiResponse<GlobalUpdateResponse>> updateMember(
            @PathVariable AccountRole role,
            // @InitBinder("memberUpdateRequest")와 변수명 일치 필수
            @Valid @RequestBody MemberUpdateRequest memberUpdateRequest,
            BindingResult bindingResult,
            @CurrentAccount CurrentAccountDTO currentAccountDTO) {

        WriteMemberService service = memberStrategyFactory.getWriteService(role);
        // Dirty Checking은 Service Layer(@Transactional) 내부에서 Entity의 상태 변경을 통해 수행됩니다.
        GlobalUpdateResponse response = service.updateMember(memberUpdateRequest, currentAccountDTO.loginId());
        return restApiController.createRestResponse(response);
    }

    @Operation(summary = "회원 비활성화")
    @PatchMapping(value = "/inactive")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RestApiResponse<GlobalInactiveResponse>> inactiveMember(
            @PathVariable AccountRole role,
            @CurrentAccount CurrentAccountDTO currentAccountDTO) {

        WriteMemberService service = memberStrategyFactory.getWriteService(role);
        GlobalInactiveResponse response = service.deActiveMember(currentAccountDTO.loginId());
        return restApiController.createRestResponse(response);
    }
}