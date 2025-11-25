package gyun.sample.domain.member.api;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.payload.dto.MemberListRequestDTO;
import gyun.sample.domain.member.payload.request.MemberCreateRequest;
import gyun.sample.domain.member.payload.request.MemberListRequest;
import gyun.sample.domain.member.payload.request.MemberUpdateRequest;
import gyun.sample.domain.member.payload.response.MemberListResponse;
import gyun.sample.domain.member.service.MemberStrategyFactory;
import gyun.sample.domain.member.service.read.ReadMemberService;
import gyun.sample.domain.member.service.write.WriteMemberService;
import gyun.sample.domain.member.validator.MemberCreateValidator;
import gyun.sample.domain.member.validator.MemberListValidator;
import gyun.sample.domain.member.validator.MemberUserUpdateValidator;
import gyun.sample.global.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Tag(name = "MemberController", description = "통합 회원(유저/관리자) 뷰/API")
@Controller
@RequestMapping(value = "/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberStrategyFactory memberStrategyFactory;

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

    @Operation(summary = "회원 생성 폼 뷰")
    @GetMapping(value = "/{role}/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or #role.name() == 'USER'")
    public String createMemberForm(@PathVariable AccountRole role, Model model) {
        if (!model.containsAttribute("memberCreateRequest")) {
            model.addAttribute("memberCreateRequest", new MemberCreateRequest(null, null, null, role, null));
        }
        model.addAttribute("role", role);
        return "member/create";
    }

    @Operation(summary = "회원 생성 처리")
    @PostMapping(value = "/{role}/create")
    // [수정] 위와 동일하게 안전한 방식으로 변경
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or #role.name() == 'USER'")
    public String createMember(
            @Parameter(description = "Account Role", example = "USER") @PathVariable AccountRole role,
            @Valid @ModelAttribute("memberCreateRequest") MemberCreateRequest memberCreateRequest,
            BindingResult bindingResult, // AOP 감지용
            RedirectAttributes redirectAttributes) {

        WriteMemberService service = memberStrategyFactory.getWriteService(role);
        service.createMember(memberCreateRequest);

        redirectAttributes.addFlashAttribute("message", "회원 생성이 완료되었습니다.");
        return "redirect:/member/" + role.name().toLowerCase() + "/list";
    }

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
        model.addAttribute("request", memberListRequest);

        return "member/list";
    }

    @Operation(summary = "회원 상세 조회 뷰")
    @GetMapping(value = "/{role}/detail/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or (#role.name() == 'USER' and @memberGuard.checkAccess(#id, principal))")
    public String getMemberDetail(
            @PathVariable AccountRole role,
            @PathVariable Long id,
            @AuthenticationPrincipal PrincipalDetails principal,
            Model model) {

        ReadMemberService service = memberStrategyFactory.getReadService(role);
        // 상세 조회 로직... (기존 코드에 없어서 템플릿 경로만 유추하여 추가함)
        // DetailMemberResponse response = service.getDetail(id);
        // model.addAttribute("member", response);

        // 임시로 list로 리다이렉트 (구현 시 위 주석 해제)
        return "member/detail";
    }

    @Operation(summary = "회원 정보 수정 처리")
    @PostMapping(value = "/{role}/update")
    @PreAuthorize("isAuthenticated()") // 로그인 사용자만
    public String updateMember(
            @PathVariable AccountRole role,
            @Valid @ModelAttribute("memberUpdateRequest") MemberUpdateRequest memberUpdateRequest,
            BindingResult bindingResult,
            @AuthenticationPrincipal PrincipalDetails principal,
            RedirectAttributes redirectAttributes) {

        WriteMemberService service = memberStrategyFactory.getWriteService(role);
        service.updateMember(memberUpdateRequest, principal.getUsername());

        redirectAttributes.addFlashAttribute("message", "정보가 수정되었습니다.");
        // 수정 후 프로필 혹은 상세 페이지로 이동
        return "redirect:/account/profile";
    }

    @Operation(summary = "회원 탈퇴/비활성화 처리")
    @PostMapping(value = "/{role}/inactive")
    @PreAuthorize("isAuthenticated()")
    public String inactiveMember(
            @PathVariable AccountRole role,
            @AuthenticationPrincipal PrincipalDetails principal) {

        WriteMemberService service = memberStrategyFactory.getWriteService(role);
        service.deActiveMember(principal.getUsername());

        return "redirect:/logout";
    }
}