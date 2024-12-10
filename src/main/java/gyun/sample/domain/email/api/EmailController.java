package gyun.sample.domain.email.api;

import gyun.sample.domain.email.service.EmailService;
import gyun.sample.global.api.RestApiController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
@Tag(name = "EmailController", description = "이메일 관련 api")
public class EmailController {

    private final EmailService emailService;
    private final RestApiController restApiController;

    @PostMapping("/send")
    @Operation(summary = "이메일 보내기")
    public ResponseEntity<String> sendEmail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String text) {
        emailService.sendEmail(to, subject, text);
        return restApiController.createSuccessRestResponse("Email sent successfully");
    }
}