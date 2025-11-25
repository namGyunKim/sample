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

/**
 * 타임리프 뷰를 반환하는 컨트롤러
 * - @Controller 어노테이션을 사용하므로 BindingAdvice(AOP)가 적용되지 않습니다.
 * - 따라서 BindingResult를 직접 확인하여 에러 발생 시 입력 폼으로 되돌려보냅니다.
 */
@Slf4j
@Tag(name = "MemberController", description = "회원 관리 (전략 패턴 적용)")
@Controller
@RequestMapping(value = "/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberStrategyFactory memberStrategyFactory;

    private final MemberCreateValidator memberCreateValidator;
    private final MemberListValidator memberListValidator;
    private final MemberUserUpdateValidator memberUserUpdateValidator;

    // Validator 등록 (변수명 일치 필수)
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

    @Operation(summary = "회원 생성 폼")
    @GetMapping(value = "/{role}/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public String createMemberForm(@PathVariable AccountRole role, Model model) {
        if (!model.containsAttribute("memberCreateRequest")) {
            model.addAttribute("memberCreateRequest", new MemberCreateRequest(null, null, null, role, null));
        }
        model.addAttribute("role", role);
        return "member/create";
    }

    @Operation(summary = "회원 생성 처리")
    @PostMapping(value = "/{role}/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public String createMember(
            @PathVariable AccountRole role,
            @Valid @ModelAttribute("memberCreateRequest") MemberCreateRequest memberCreateRequest,
            BindingResult bindingResult, // AOP 미적용: 직접 처리
            RedirectAttributes redirectAttributes,
            Model model) {

        // 유효성 검사 실패 시: 입력 폼으로 다시 이동 (에러 메시지 포함)
        if (bindingResult.hasErrors()) {
            model.addAttribute("role", role);
            return "member/create";
        }

        WriteMemberService service = memberStrategyFactory.getWriteService(role);
        service.createMember(memberCreateRequest);

        redirectAttributes.addFlashAttribute("message", "회원이 성공적으로 생성되었습니다.");
        return "redirect:/member/" + role.name().toLowerCase() + "/list";
    }

    @Operation(summary = "회원 목록 조회")
    @GetMapping(value = "/{role}/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public String getMemberList(
            @PathVariable AccountRole role,
            @Valid @ModelAttribute("memberListRequest") MemberListRequest memberListRequest,
            BindingResult bindingResult,
            Model model) {

        // 목록 조회 검색 조건 에러 시 처리
        if (bindingResult.hasErrors()) {
            model.addAttribute("role", role);
            model.addAttribute("request", memberListRequest);
            model.addAttribute("memberPage", Page.empty());
            return "member/list";
        }

        ReadMemberService service = memberStrategyFactory.getReadService(role);
        // Record -> DTO 변환
        var listRequestDTO = new MemberListRequestDTO(memberListRequest);
        Page<MemberListResponse> memberPage = service.getList(listRequestDTO);

        model.addAttribute("role", role);
        model.addAttribute("memberPage", memberPage);
        model.addAttribute("request", memberListRequest);

        return "member/list";
    }

    @Operation(summary = "회원 상세 조회")
    @GetMapping(value = "/{role}/detail/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or (#role.name() == 'USER' and @memberGuard.checkAccess(#id, principal))")
    public String getMemberDetail(
            @PathVariable AccountRole role,
            @PathVariable Long id,
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

        ReadMemberService service = memberStrategyFactory.getReadService(role);
        // JPA 더티 체킹을 사용하므로 엔티티 조회 후 DTO 변환하여 뷰에 전달
        Member member = service.getByLoginIdAndRole(principal.getUsername(), role);

        if (!model.containsAttribute("memberUpdateRequest")) {
            model.addAttribute("memberUpdateRequest", new MemberUpdateRequest(member.getNickName(), null));
        }
        model.addAttribute("currentMember", new DetailMemberResponse(member));
        model.addAttribute("role", role);

        return "member/update";
    }

    @Operation(summary = "회원 정보 수정 처리")
    @PostMapping(value = "/{role}/update")
    @PreAuthorize("isAuthenticated()")
    public String updateMember(
            @PathVariable AccountRole role,
            @Valid @ModelAttribute("memberUpdateRequest") MemberUpdateRequest memberUpdateRequest,
            BindingResult bindingResult,
            @AuthenticationPrincipal PrincipalDetails principal,
            RedirectAttributes redirectAttributes,
            Model model) {

        // 유효성 검사 실패 시 수정 페이지로 복귀
        if (bindingResult.hasErrors()) {
            ReadMemberService service = memberStrategyFactory.getReadService(role);
            Member member = service.getByLoginIdAndRole(principal.getUsername(), role);

            model.addAttribute("currentMember", new DetailMemberResponse(member));
            model.addAttribute("role", role);
            return "member/update";
        }

        WriteMemberService service = memberStrategyFactory.getWriteService(role);
        service.updateMember(memberUpdateRequest, principal.getUsername());

        redirectAttributes.addFlashAttribute("message", "정보가 수정되었습니다.");
        return "redirect:/account/profile";
    }

    @Operation(summary = "회원 탈퇴/비활성화")
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