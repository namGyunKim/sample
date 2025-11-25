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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Tag(name = "MemberController", description = "통합 회원(유저/관리자) 뷰/API")
@Controller // @RestController -> @Controller로 변경
@RequestMapping(value = "/member") // "/api/member/{role}" -> "/member" 또는 역할별 분리
@RequiredArgsConstructor
public class MemberController {

    // RestApiController 제거
    // private final RestApiController restApiController;
    private final MemberStrategyFactory memberStrategyFactory;

    // 통합 Validators
    private final MemberCreateValidator memberCreateValidator;
    private final MemberListValidator memberListValidator;
    private final MemberUserUpdateValidator memberUserUpdateValidator;

    // AOP에서 BindingResult를 처리하므로, 여기서는 @InitBinder만 남깁니다.
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

    /**
     * 회원 생성 폼 뷰
     */
    @Operation(summary = "회원 생성 폼 뷰")
    @GetMapping(value = "/{role}/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or #role == T(gyun.sample.domain.account.enums.AccountRole).USER")
    public String createMemberForm(@PathVariable AccountRole role, Model model) {
        // Thymeleaf 폼 바인딩을 위해 빈 객체 전달
        if (!model.containsAttribute("memberCreateRequest")) {
            model.addAttribute("memberCreateRequest", new MemberCreateRequest(null, null, null, role, null));
        }
        model.addAttribute("role", role);
        return "member/create"; // src/main/resources/templates/member/create.html
    }

    /**
     * 회원 생성 처리 (POST)
     */
    @Operation(summary = "회원 생성 처리")
    @PostMapping(value = "/{role}/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or #role == T(gyun.sample.domain.account.enums.AccountRole).USER")
    public String createMember(
            @Parameter(description = "Account Role", example = "USER") @PathVariable AccountRole role,
            // @ModelAttribute로 변경하여 폼 데이터 바인딩
            @Valid @ModelAttribute("memberCreateRequest") MemberCreateRequest memberCreateRequest,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        // BindingAdvice가 오류를 GlobalException으로 변환하기 때문에,
        // 여기서는 BindingResult를 직접 체크하는 대신, ExceptionAdvice의 @ExceptionHandler를 활용합니다.
        // 다만, 폼 기반에서는 오류 발생 시 폼으로 다시 돌아가는 것이 일반적이므로,
        // BindingAdvice의 로직을 수정하지 않는다면, 오류 발생 시 예외가 던져져 JSON 응답으로 처리됩니다.
        // *클린 아키텍처를 위해 BindingAdvice를 유지하고, Thymeleaf에서는 예외 핸들러를 수정하는 것을 권장*

        WriteMemberService service = memberStrategyFactory.getWriteService(role);
        service.createMember(memberCreateRequest);

        // 성공 메시지를 담아 리다이렉트
        redirectAttributes.addFlashAttribute("message", "회원 생성이 완료되었습니다.");
        return "redirect:/member/" + role.name().toLowerCase() + "/list";
    }

    /**
     * 회원 목록 조회 뷰
     */
    @Operation(summary = "회원 목록 조회 뷰")
    @GetMapping(value = "/{role}/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public String getMemberList(
            @PathVariable AccountRole role,
            @Valid @ModelAttribute("memberListRequest") MemberListRequest memberListRequest,
            BindingResult bindingResult,
            Model model) {

        ReadMemberService service = memberStrategyFactory.getReadService(role);
        var listRequestDTO = new MemberListRequestDTO(memberListRequest);
        Page<MemberListResponse> memberPage = service.getList(listRequestDTO);

        model.addAttribute("role", role);
        model.addAttribute("memberPage", memberPage);
        // 페이징, 필터링을 위한 요청 객체도 모델에 추가
        model.addAttribute("request", memberListRequest);

        return "member/list"; // src/main/resources/templates/member/list.html
    }

    /**
     * 회원 상세 조회 뷰
     */
    @Operation(summary = "회원 상세 조회 뷰")
    @GetMapping(value = "/{role}/detail/{id}")
    @PreAuthorize("isAuthenticated()")
    public String getMemberDetail(
            @PathVariable AccountRole role,
            @PathVariable long id,
            Model model) {

        ReadMemberService service = memberStrategyFactory.getReadService(role);
        DetailMemberResponse response = service.getDetail(id);

        model.addAttribute("role", role);
        model.addAttribute("member", response);

        return "member/detail"; // src/main/resources/templates/member/detail.html
    }

    /**
     * 회원 정보 수정 폼 뷰
     */
    @Operation(summary = "회원 정보 수정 폼 뷰")
    @GetMapping(value = "/{role}/update")
    @PreAuthorize("isAuthenticated()")
    public String updateMemberForm(
            @PathVariable AccountRole role,
            @CurrentAccount CurrentAccountDTO currentAccountDTO,
            Model model) {
        // 현재 로그인된 사용자의 상세 정보를 가져와 폼에 바인딩할 객체를 생성
        ReadMemberService readService = memberStrategyFactory.getReadService(role);
        DetailMemberResponse detail = readService.getDetail(currentAccountDTO.id());

        if (!model.containsAttribute("memberUpdateRequest")) {
            // 기존 닉네임을 사용하여 폼 객체 생성
            MemberUpdateRequest request = new MemberUpdateRequest(detail.getProfile().nickName(), null);
            model.addAttribute("memberUpdateRequest", request);
        }
        model.addAttribute("role", role);
        model.addAttribute("currentMember", detail);
        return "member/update"; // src/main/resources/templates/member/update.html
    }


    /**
     * 회원 정보 수정 처리 (POST)
     */
    @Operation(summary = "회원 정보 수정 처리")
    @PostMapping(value = "/{role}/update")
    @PreAuthorize("isAuthenticated()")
    public String updateMember(
            @PathVariable AccountRole role,
            @Valid @ModelAttribute("memberUpdateRequest") MemberUpdateRequest memberUpdateRequest,
            BindingResult bindingResult,
            @CurrentAccount CurrentAccountDTO currentAccountDTO,
            RedirectAttributes redirectAttributes) {

        WriteMemberService service = memberStrategyFactory.getWriteService(role);
        service.updateMember(memberUpdateRequest, currentAccountDTO.loginId());

        redirectAttributes.addFlashAttribute("message", "정보 수정이 완료되었습니다.");
        return "redirect:/member/" + role.name().toLowerCase() + "/detail/" + currentAccountDTO.id();
    }

    /**
     * 회원 비활성화 (탈퇴) 처리
     */
    @Operation(summary = "회원 비활성화 처리 (탈퇴)")
    @PostMapping(value = "/{role}/inactive")
    @PreAuthorize("isAuthenticated()")
    public String inactiveMember(
            @PathVariable AccountRole role,
            @CurrentAccount CurrentAccountDTO currentAccountDTO,
            RedirectAttributes redirectAttributes) {

        WriteMemberService service = memberStrategyFactory.getWriteService(role);
        service.deActiveMember(currentAccountDTO.loginId());

        // 로그아웃 후 로그인 페이지로 리다이렉트
        redirectAttributes.addFlashAttribute("logoutMessage", "회원 탈퇴가 완료되었습니다.");
        return "redirect:/logout";
    }
}