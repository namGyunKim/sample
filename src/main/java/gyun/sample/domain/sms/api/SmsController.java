package gyun.sample.domain.sms.api;

import gyun.sample.domain.sms.payload.request.PasswordGetRequest;
import gyun.sample.domain.sms.payload.request.SMSRequest;
import gyun.sample.domain.sms.service.SmsService;
import gyun.sample.global.api.RestApiController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sms")
@Tag(name = "SmsController", description = "SMS 인증 관련 기능 api")
@RequiredArgsConstructor
public class SmsController {

    private final SmsService smsService;
    private final RestApiController restApiController;

    @Operation(summary = "인증번호 전송", description = "지정된 번호로 인증번호를 전송합니다. 실패시 coolSMS API 에러 메시지를 반환합니다.")
    @PostMapping("/send")
    public ResponseEntity<String> sendSms(
            @RequestParam String countryCode,
            @RequestParam String phoneNumber) {

        String result = smsService.sendVerificationCode(countryCode, phoneNumber);
        if (result.equals("success")) {
            return restApiController.createSuccessRestResponse("인증번호가 전송되었습니다.");
        } else {
            return restApiController.createFailRestResponse(result);
        }
    }

    @Operation(summary = "인증번호 검증", description = "한번 검증한 코드는 재활용 불가능.")
    @PostMapping("/verify")
    public ResponseEntity<String> verify(@Valid @RequestBody SMSRequest smsRequest, BindingResult bindingResult) {

        boolean result = smsService.verify(smsRequest);
        return restApiController.createSuccessRestResponse(result);
    }

    @Operation(summary = "return loginId", description = "한번 검증한 코드는 재활용 불가능.")
    @PostMapping("/verify/loginId")
    public ResponseEntity<String> verifyLoginId(@Valid @RequestBody SMSRequest smsRequest, BindingResult bindingResult) {

        String result = smsService.verifyWithLoginId(smsRequest);
        return restApiController.createSuccessRestResponse(result);
    }

    @Operation(summary = "비밀번호 찾기 true is pass", description = "한번 검증한 코드는 재활용 불가능.")
    @PostMapping("/verify/password")
    public ResponseEntity<String> findPassword(@Valid @RequestBody PasswordGetRequest smsRequest, BindingResult bindingResult) {

        boolean result = smsService.findPassword(smsRequest);
        return restApiController.createSuccessRestResponse(result);
    }
}
