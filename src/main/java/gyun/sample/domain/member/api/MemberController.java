package gyun.sample.domain.member.api;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.payload.dto.MemberListRequestDTO;
import gyun.sample.domain.member.payload.request.MemberCreateRequest;
import gyun.sample.domain.member.payload.request.MemberListRequest;
import gyun.sample.domain.member.payload.request.MemberRoleUpdateRequest;
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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Tag(name = "MemberController", description = "회원 관리 (전략 패턴 적용)")
@Controller
@RequestMapping(value = "/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberStrategyFactory memberStrategyFactory;

    // Validators
    private final MemberCreateValidator memberCreateValidator;
    private final MemberListValidator memberListValidator;
    private final MemberUserUpdateValidator memberUserUpdateValidator;

    // Security Context Repository
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    // === InitBinders ===
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

    // === Views & Actions ===

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
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

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

        if (bindingResult.hasErrors()) {
            return "member/list";
        }

        ReadMemberService service = memberStrategyFactory.getReadService(role);
        Page<MemberListResponse> memberPage = service.getList(new MemberListRequestDTO(memberListRequest));

        model.addAttribute("role", role);
        model.addAttribute("memberPage", memberPage);
        model.addAttribute("request", memberListRequest);

        return "member/list";
    }

    @Operation(summary = "회원 상세 조회")
    @GetMapping(value = "/{role}/detail/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or (#role.name() == 'USER' and @memberGuard.checkCreateRole(#role))")
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
    @GetMapping(value = "/{role}/update/{id}")
    @PreAuthorize("isAuthenticated()")
    public String updateMemberForm(
            @PathVariable AccountRole role,
            @PathVariable Long id,
            @AuthenticationPrincipal PrincipalDetails principal,
            Model model) {

        ReadMemberService service = memberStrategyFactory.getReadService(role);
        DetailMemberResponse targetMember = service.getDetail(id);

        checkPermission(principal, targetMember.getProfile().role(), targetMember.getProfile().id());

        if (!model.containsAttribute("memberUpdateRequest")) {
            model.addAttribute("memberUpdateRequest", new MemberUpdateRequest(targetMember.getProfile().nickName()));
        }

        model.addAttribute("currentMember", targetMember);
        model.addAttribute("role", role);
        model.addAttribute("targetId", id);

        return "member/update";
    }

    @Operation(summary = "회원 정보 수정 처리")
    @PostMapping(value = "/{role}/update/{id}")
    @PreAuthorize("isAuthenticated()")
    public String updateMember(
            @PathVariable AccountRole role,
            @PathVariable Long id,
            @Valid @ModelAttribute("memberUpdateRequest") MemberUpdateRequest memberUpdateRequest,
            BindingResult bindingResult,
            @AuthenticationPrincipal PrincipalDetails principal,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) {

        ReadMemberService readService = memberStrategyFactory.getReadService(role);
        DetailMemberResponse targetMember = readService.getDetail(id);

        checkPermission(principal, targetMember.getProfile().role(), targetMember.getProfile().id());

        if (bindingResult.hasErrors()) {
            model.addAttribute("currentMember", targetMember);
            model.addAttribute("role", role);
            model.addAttribute("targetId", id);
            return "member/update";
        }

        WriteMemberService writeService = memberStrategyFactory.getWriteService(role);
        writeService.updateMember(memberUpdateRequest, targetMember.getProfile().loginId());

        if (principal.getId().equals(id)) {
            refreshSession(request, response, principal.getUsername(), role);
        }

        redirectAttributes.addFlashAttribute("message", "정보가 성공적으로 수정되었습니다.");

        return principal.getId().equals(id) ? "redirect:/account/profile" :
                "redirect:/member/" + role.name().toLowerCase() + "/detail/" + id;
    }

    @Operation(summary = "회원 탈퇴/비활성화")
    @PostMapping(value = "/{role}/inactive/{id}")
    @PreAuthorize("isAuthenticated()")
    public String inactiveMember(
            @PathVariable AccountRole role,
            @PathVariable Long id,
            @AuthenticationPrincipal PrincipalDetails principal,
            RedirectAttributes redirectAttributes) {

        ReadMemberService readService = memberStrategyFactory.getReadService(role);
        DetailMemberResponse targetMember = readService.getDetail(id);

        checkPermission(principal, targetMember.getProfile().role(), targetMember.getProfile().id());

        WriteMemberService writeService = memberStrategyFactory.getWriteService(role);
        writeService.deActiveMember(targetMember.getProfile().loginId());

        if (principal.getId().equals(id)) {
            return "redirect:/logout";
        } else {
            redirectAttributes.addFlashAttribute("message", "해당 회원이 비활성화 처리되었습니다.");
            return "redirect:/member/" + role.name().toLowerCase() + "/list";
        }
    }

    // [수정] 회원 등급 변경 폼 (최고 관리자 전용)
    @Operation(summary = "회원 등급 변경 폼")
    @GetMapping(value = "/{role}/role-update/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')") // 최고 관리자만 접근 가능
    public String updateMemberRoleForm(
            @PathVariable AccountRole role,
            @PathVariable Long id,
            @AuthenticationPrincipal PrincipalDetails principal,
            Model model) {

        ReadMemberService service = memberStrategyFactory.getReadService(role);
        DetailMemberResponse targetMember = service.getDetail(id);

        // 권한 변경 유효성 검사 (본인 변경 불가 등)
        validateRoleChangePermission(principal, targetMember.getProfile().id());

        if (!model.containsAttribute("memberRoleUpdateRequest")) {
            model.addAttribute("memberRoleUpdateRequest", new MemberRoleUpdateRequest(targetMember.getProfile().role()));
        }

        // 변경 가능한 모든 권한 목록 (GUEST 제외)
        List<AccountRole> assignableRoles = Arrays.stream(AccountRole.values())
                .filter(r -> r != AccountRole.GUEST)
                .toList();

        model.addAttribute("targetMember", targetMember);
        model.addAttribute("assignableRoles", assignableRoles);
        model.addAttribute("role", role); // 현재 조회 중인 경로 상의 role (뒤로가기 용)
        model.addAttribute("targetId", id);

        return "member/role_update";
    }

    // [수정] 회원 등급 변경 처리 (최고 관리자 전용)
    @Operation(summary = "회원 등급 변경 처리")
    @PostMapping(value = "/{role}/role-update/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String updateMemberRole(
            @PathVariable AccountRole role, // 현재 보고 있는 리스트의 Role (ex: USER)
            @PathVariable Long id,
            @Valid @ModelAttribute("memberRoleUpdateRequest") MemberRoleUpdateRequest request,
            BindingResult bindingResult,
            @AuthenticationPrincipal PrincipalDetails principal,
            RedirectAttributes redirectAttributes,
            Model model) {

        ReadMemberService readService = memberStrategyFactory.getReadService(role);
        DetailMemberResponse targetMember = readService.getDetail(id);

        validateRoleChangePermission(principal, targetMember.getProfile().id());

        if (bindingResult.hasErrors()) {
            List<AccountRole> assignableRoles = Arrays.stream(AccountRole.values())
                    .filter(r -> r != AccountRole.GUEST)
                    .toList();
            model.addAttribute("targetMember", targetMember);
            model.addAttribute("assignableRoles", assignableRoles);
            model.addAttribute("role", role);
            model.addAttribute("targetId", id);
            return "member/role_update";
        }

        WriteMemberService writeService = memberStrategyFactory.getWriteService(role);
        writeService.updateMemberRole(targetMember.getProfile().loginId(), request.role());

        redirectAttributes.addFlashAttribute("message", "회원 등급이 변경되었습니다.");

        // [중요] 등급이 변경되면 기존 경로(/member/user/...)로 리다이렉트 시
        // ReadService가 해당 회원을 찾지 못할 수 있음 (Specification 필터링 때문).
        // 따라서 변경된 등급에 맞는 상세 페이지로 리다이렉트해야 함.
        String newRolePath = request.role().name().toLowerCase();

        return "redirect:/member/" + newRolePath + "/detail/" + id;
    }

    // [수정] 권한 변경 유효성 검사: 최고 관리자만 가능하며, 본인의 권한은 변경할 수 없음
    private void validateRoleChangePermission(PrincipalDetails principal, Long targetId) {
        if (principal.getRole() != AccountRole.SUPER_ADMIN) {
            throw new AccessDeniedException("최고 관리자만 회원 등급을 변경할 수 있습니다.");
        }
        if (principal.getId().equals(targetId)) {
            throw new AccessDeniedException("자신의 등급은 변경할 수 없습니다.");
        }
    }

    // 권한 체크 헬퍼 (정보 수정, 탈퇴 등)
    private void checkPermission(PrincipalDetails principal, AccountRole targetRole, Long targetId) {
        if (principal.getId().equals(targetId)) return; // 본인
        if (principal.getRole() == AccountRole.SUPER_ADMIN) return; // 최고관리자
        if (principal.getRole() == AccountRole.ADMIN && targetRole == AccountRole.USER) return; // 관리자가 유저 관리

        throw new AccessDeniedException("해당 작업에 대한 권한이 없습니다.");
    }

    // 세션 갱신 로직
    private void refreshSession(HttpServletRequest request, HttpServletResponse response, String loginId, AccountRole role) {
        ReadMemberService readService = memberStrategyFactory.getReadService(role);
        Member updatedMember = readService.getByLoginIdAndRole(loginId, role);

        PrincipalDetails newPrincipal = new PrincipalDetails(updatedMember);
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                newPrincipal,
                newPrincipal.getPassword(),
                newPrincipal.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(newAuth);
        securityContextRepository.saveContext(SecurityContextHolder.getContext(), request, response);
    }
}