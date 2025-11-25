package gyun.sample.domain.log.api;

import gyun.sample.domain.log.payload.response.MemberLogResponse;
import gyun.sample.domain.log.service.read.ReadMemberLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String searchWord, // 검색어 파라미터 추가
            Model model) {

        Page<MemberLogResponse> logPage = readMemberLogService.getMemberLogs(page, size, searchWord);

        model.addAttribute("logPage", logPage);
        model.addAttribute("searchWord", searchWord); // 뷰에서 검색어 유지

        return "log/member/list";
    }
}