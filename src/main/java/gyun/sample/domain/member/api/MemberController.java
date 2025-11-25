package gyun.sample.domain.member.api;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.Member;
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

    // [수정] 회원 생성은 관리자만 가능 (일반 유저는 소셜 로그인으로 자동 가입됨)
    @Operation(summary = "회원 생성 폼 뷰 (관리자 전용)")
    @GetMapping(value = "/{role}/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public String createMemberForm(@PathVariable AccountRole role, Model model) {
        if (!model.containsAttribute("memberCreateRequest")) {
            model.addAttribute("memberCreateRequest", new MemberCreateRequest(null, null, null, role, null));
        }
        model.addAttribute("role", role);
        return "member/create";
    }

    // [수정] 회원 생성 처리 (관리자 전용)
    @Operation(summary = "회원 생성 처리 (관리자 전용)")
    @PostMapping(value = "/{role}/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
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
    // 관리자이거나, 본인인 경우에만 접근 가능 (MemberGuard 활용)
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or (#role.name() == 'USER' and @memberGuard.checkAccess(#id, principal))")
    public String getMemberDetail(
            @PathVariable AccountRole role,
            @PathVariable Long id,
            @AuthenticationPrincipal PrincipalDetails principal,
            Model model) {

        ReadMemberService service = memberStrategyFactory.getReadService(role);
        DetailMemberResponse response = service.getDetail(id);

        model.addAttribute("member", response);
        model.addAttribute("role", role);

        return "member/detail";
    }

    @Operation(summary = "회원 정보 수정 폼")
    @GetMapping(value = "/{role}/update")
    @PreAuthorize("isAuthenticated()")
    public String updateMemberForm(
            @PathVariable AccountRole role,
            @AuthenticationPrincipal PrincipalDetails principal,
            Model model) {

        // 현재 로그인한 사용자의 정보를 가져옴
        ReadMemberService service = memberStrategyFactory.getReadService(role);
        Member member = service.getByLoginIdAndRole(principal.getUsername(), role);

        // 수정 폼에 보여줄 객체 세팅
        if (!model.containsAttribute("memberUpdateRequest")) {
            model.addAttribute("memberUpdateRequest", new MemberUpdateRequest(member.getNickName(), null));
        }

        // 화면 표시용 현재 정보
        model.addAttribute("currentMember", new DetailMemberResponse(member));
        model.addAttribute("role", role);

        return "member/update";
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