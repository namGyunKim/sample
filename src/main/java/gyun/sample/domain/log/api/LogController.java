package gyun.sample.domain.log.api;

import gyun.sample.domain.log.payload.request.MemberLogRequest;
import gyun.sample.domain.log.payload.response.MemberLogResponse;
import gyun.sample.domain.log.service.read.ReadMemberLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Tag(name = "LogController", description = "시스템 로그 관리")
@Controller
@RequestMapping("/log")
@RequiredArgsConstructor
public class LogController {

    private final ReadMemberLogService readMemberLogService;

    @Operation(summary = "회원 활동 로그 목록 페이지")
    @GetMapping("/member")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public String memberLogList(
            // @ModelAttribute("request") 이름을 명시하여 BindingResult가 "request" 객체에 바인딩되도록 함
//            이러면 뷰에서request 라는 이름으로 인식됨
            @Valid @ModelAttribute("request") MemberLogRequest request,
            BindingResult bindingResult,
            Model model) {

        // 1. 유효성 검증 실패 시 (예: page < 1, size < 1)
        if (bindingResult.hasErrors()) {
            // 에러가 있어도 검색 조건 등은 유지되어야 하므로 request는 그대로 전달
            // 빈 페이지 객체 전달 (화면 렌더링 오류 방지)
            model.addAttribute("logPage", Page.empty());
            return "log/member/list";
        }

        // 2. 정상 조회
        Page<MemberLogResponse> logPage = readMemberLogService.getMemberLogs(request);

        model.addAttribute("logPage", logPage);

        return "log/member/list";
    }
}