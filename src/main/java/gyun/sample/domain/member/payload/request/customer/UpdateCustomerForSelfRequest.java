package gyun.sample.domain.member.payload.request.customer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UpdateCustomerForSelfRequest(
        @NotBlank(message = "닉네임을 입력해주세요.")
        @Schema(description = "닉네임", example = "남균찡")
        String nickName,
        @Schema(description = "비밀번호", example = "  ")
        String password
) {
}
