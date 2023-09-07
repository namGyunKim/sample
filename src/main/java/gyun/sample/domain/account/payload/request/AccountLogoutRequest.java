package gyun.sample.domain.account.payload.request;

import jakarta.validation.constraints.NotBlank;

public record AccountLogoutRequest(
        @NotBlank(message = "리프레쉬 토큰은 필수 입력값 입니다.")
        String refreshToken) {

}
