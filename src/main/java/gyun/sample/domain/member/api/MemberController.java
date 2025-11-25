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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
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

    // BindingResult는 AOP에서 처리하므로 컨트롤러 내부 로직에서는 신경 쓰지 않아도 됨
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
            BindingResult bindingResult, // AOP 감지용 필수 파라미터
            RedirectAttributes redirectAttributes) {

        // AOP(BindingAdvice)가 에러를 감지하면 예외를 던지므로
        // 여기까지 코드가 도달했다면 유효성 검증은 통과한 것임

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

    // ... 기타 메서드 (상세, 수정 등) 생략 ...
}