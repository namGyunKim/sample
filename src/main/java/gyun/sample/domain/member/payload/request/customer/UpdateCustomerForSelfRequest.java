package gyun.sample.domain.member.payload.request.customer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UpdateCustomerForSelfRequest(
        @NotBlank(message = "이름을 입력해주세요.")
        @Schema(description = "이름", example = "김남균")
        String name,
        @Schema(description = "비밀번호", example = "  ")
        String password
) {
}
