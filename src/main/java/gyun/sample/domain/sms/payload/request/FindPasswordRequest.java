package gyun.sample.domain.sms.payload.request;

import jakarta.validation.constraints.NotBlank;

public record FindPasswordRequest(
        @NotBlank(message = "국가 코드를 입력해주세요.")
        String countryCode,
        @NotBlank(message = "전화번호를 입력해주세요.")
        String phoneNumber,
        @NotBlank(message = "인증번호를 입력해주세요.")
        String verificationCode,
        @NotBlank(message = "로그인 아이디를 입력해주세요.")
        String loginId
) {
}
