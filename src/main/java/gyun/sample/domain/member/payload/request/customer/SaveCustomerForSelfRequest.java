package gyun.sample.domain.member.payload.request.customer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SaveCustomerForSelfRequest(
        @NotBlank(message = "로그인 아이디를 입력해주세요.")
        @Schema(description = "로그인 아이디", example = "skarbs01")
        String loginId,
        @NotBlank(message = "이름을 입력해주세요.")
        @Schema(description = "이름", example = "김남균")
        String name,
        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Schema(description = "비밀번호", example = "Password1!")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,15}$",
                message = "비밀번호는 8~15자의 영어 대소문자, 숫자, 특수문자 조합으로 설정되어야 합니다.")
        String password
) {
}
