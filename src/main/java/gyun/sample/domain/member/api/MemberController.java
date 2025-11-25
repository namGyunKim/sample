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

    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

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
            model.addAttribute("role", role);
            model.addAttribute("request", memberListRequest);
            model.addAttribute("memberPage", Page.empty());
            return "member/list";
        }

        ReadMemberService service = memberStrategyFactory.getReadService(role);
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

    // [수정] URL에 대상 ID({id})를 포함하도록 변경하여, 관리자가 다른 회원을 수정할 수 있도록 함
    @Operation(summary = "회원 정보 수정 폼")
    @GetMapping(value = "/{role}/update/{id}")
    @PreAuthorize("isAuthenticated()")
    public String updateMemberForm(
            @PathVariable AccountRole role,
            @PathVariable Long id,
            @AuthenticationPrincipal PrincipalDetails principal,
            Model model) {

        // 1. 수정 대상 회원 조회
        ReadMemberService service = memberStrategyFactory.getReadService(role);
        DetailMemberResponse targetMember = service.getDetail(id);

        // 2. 수정 권한 체크 (본인, 슈퍼관리자, 관리자->유저)
        checkUpdatePermission(principal, targetMember.getProfile().role(), targetMember.getProfile().id());

        if (!model.containsAttribute("memberUpdateRequest")) {
            model.addAttribute("memberUpdateRequest", new MemberUpdateRequest(targetMember.getProfile().nickName()));
        }
        model.addAttribute("currentMember", targetMember);
        model.addAttribute("role", role);
        model.addAttribute("targetId", id); // 폼 Action URL 생성을 위해 ID 전달

        return "member/update";
    }

    // [수정] URL에 대상 ID({id})를 포함하여 처리
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

        // 1. 수정 권한 체크
        checkUpdatePermission(principal, targetMember.getProfile().role(), targetMember.getProfile().id());

        if (bindingResult.hasErrors()) {
            model.addAttribute("currentMember", targetMember);
            model.addAttribute("role", role);
            model.addAttribute("targetId", id);
            return "member/update";
        }

        // 2. 정보 수정 실행 (WriteService는 loginId를 식별자로 사용)
        WriteMemberService writeService = memberStrategyFactory.getWriteService(role);
        writeService.updateMember(memberUpdateRequest, targetMember.getProfile().loginId());

        // 3. 세션 갱신 (본인이 본인 정보를 수정한 경우에만 수행)
        if (principal.getId().equals(id)) {
            Member updatedMember = readService.getByLoginIdAndRole(principal.getUsername(), role);
            PrincipalDetails newPrincipal = new PrincipalDetails(updatedMember);
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    newPrincipal,
                    newPrincipal.getPassword(),
                    newPrincipal.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(newAuth);
            securityContextRepository.saveContext(SecurityContextHolder.getContext(), request, response);
        }

        redirectAttributes.addFlashAttribute("message", "정보가 수정되었습니다.");

        // 본인이면 프로필로, 관리자가 타인을 수정한 경우 해당 유저 상세 페이지로 이동
        if (principal.getId().equals(id)) {
            return "redirect:/account/profile";
        } else {
            return "redirect:/member/" + role.name().toLowerCase() + "/detail/" + id;
        }
    }

    /**
     * 수정/탈퇴 권한 체크 로직
     */
    private void checkUpdatePermission(PrincipalDetails principal, AccountRole targetRole, Long targetId) {
        // 1. 본인인 경우 허용
        if (principal.getId().equals(targetId)) {
            return;
        }

        // 2. 슈퍼 관리자는 모든 회원(관리자 포함) 수정 가능
        if (principal.getRole() == AccountRole.SUPER_ADMIN) {
            return;
        }

        // 3. 관리자는 일반 회원만 수정 가능
        if (principal.getRole() == AccountRole.ADMIN && targetRole == AccountRole.USER) {
            return;
        }

        // 그 외에는 권한 없음
        throw new AccessDeniedException("권한이 없습니다.");
    }

    @Operation(summary = "회원 탈퇴/비활성화")
    @PostMapping(value = "/{role}/inactive/{id}")
    @PreAuthorize("isAuthenticated()")
    public String inactiveMember(
            @PathVariable AccountRole role,
            @PathVariable Long id,
            @AuthenticationPrincipal PrincipalDetails principal,
            RedirectAttributes redirectAttributes) {

        // 1. 대상 회원 조회 (권한 체크를 위해 Role 등 정보 필요)
        ReadMemberService readService = memberStrategyFactory.getReadService(role);
        DetailMemberResponse targetMember = readService.getDetail(id);

        // 2. 탈퇴 권한 체크 (수정과 동일한 로직 사용)
        checkUpdatePermission(principal, targetMember.getProfile().role(), targetMember.getProfile().id());

        // 3. 탈퇴 처리 (대상 회원의 LoginId 사용)
        WriteMemberService writeService = memberStrategyFactory.getWriteService(role);
        writeService.deActiveMember(targetMember.getProfile().loginId());

        // 4. 결과 처리
        if (principal.getId().equals(id)) {
            // 본인이 탈퇴한 경우 로그아웃
            return "redirect:/logout";
        } else {
            // 관리자가 타인을 탈퇴시킨 경우 목록으로 이동
            redirectAttributes.addFlashAttribute("message", "회원이 탈퇴/비활성화 처리되었습니다.");
            return "redirect:/member/" + role.name().toLowerCase() + "/list";
        }
    }
}