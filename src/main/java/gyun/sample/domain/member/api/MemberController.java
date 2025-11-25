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
@Controller
@RequestMapping(value = "/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberStrategyFactory memberStrategyFactory;

    // 통합 Validators
    private final MemberCreateValidator memberCreateValidator;
    private final MemberListValidator memberListValidator;
    private final MemberUserUpdateValidator memberUserUpdateValidator;

    // @InitBinder 이름 매칭 필수!
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

    @Operation(summary = "회원 생성 폼 뷰")
    @GetMapping(value = "/{role}/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or #role == T(gyun.sample.domain.account.enums.AccountRole).USER")
    public String createMemberForm(@PathVariable AccountRole role, Model model) {
        if (!model.containsAttribute("memberCreateRequest")) {
            model.addAttribute("memberCreateRequest", new MemberCreateRequest(null, null, null, role, null));
        }
        model.addAttribute("role", role);
        return "member/create";
    }

    @Operation(summary = "회원 생성 처리")
    @PostMapping(value = "/{role}/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or #role == T(gyun.sample.domain.account.enums.AccountRole).USER")
    public String createMember(
            @Parameter(description = "Account Role", example = "USER") @PathVariable AccountRole role,
            @Valid @ModelAttribute("memberCreateRequest") MemberCreateRequest memberCreateRequest,
            BindingResult bindingResult, // AOP 제외됨 -> 직접 처리
            Model model,
            RedirectAttributes redirectAttributes) {

        // 타임리프에서는 에러 발생 시 예외를 던지는 게 아니라, 에러 정보를 담고 폼 뷰로 돌아가야 함
        if (bindingResult.hasErrors()) {
            model.addAttribute("role", role);
            return "member/create"; // 입력 폼으로 다시 이동 (에러 메시지 포함)
        }

        WriteMemberService service = memberStrategyFactory.getWriteService(role);
        service.createMember(memberCreateRequest);

        redirectAttributes.addFlashAttribute("message", "회원 생성이 완료되었습니다.");
        return "redirect:/member/" + role.name().toLowerCase() + "/list";
    }

    // ... (list, detail 메서드는 기존 로직 유지)

    @Operation(summary = "회원 정보 수정 폼 뷰")
    @GetMapping(value = "/{role}/update")
    @PreAuthorize("isAuthenticated()")
    public String updateMemberForm(
            @PathVariable AccountRole role,
            @CurrentAccount CurrentAccountDTO currentAccountDTO,
            Model model) {
        ReadMemberService readService = memberStrategyFactory.getReadService(role);
        DetailMemberResponse detail = readService.getDetail(currentAccountDTO.id());

        if (!model.containsAttribute("memberUpdateRequest")) {
            MemberUpdateRequest request = new MemberUpdateRequest(detail.getProfile().nickName(), null);
            model.addAttribute("memberUpdateRequest", request);
        }
        model.addAttribute("role", role);
        model.addAttribute("currentMember", detail);
        return "member/update";
    }


    @Operation(summary = "회원 정보 수정 처리")
    @PostMapping(value = "/{role}/update")
    @PreAuthorize("isAuthenticated()")
    public String updateMember(
            @PathVariable AccountRole role,
            @Valid @ModelAttribute("memberUpdateRequest") MemberUpdateRequest memberUpdateRequest,
            BindingResult bindingResult,
            @CurrentAccount CurrentAccountDTO currentAccountDTO,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            // 에러 발생 시 다시 폼 화면 렌더링을 위해 필요한 데이터 재조회
            ReadMemberService readService = memberStrategyFactory.getReadService(role);
            DetailMemberResponse detail = readService.getDetail(currentAccountDTO.id());

            model.addAttribute("role", role);
            model.addAttribute("currentMember", detail);
            return "member/update";
        }

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