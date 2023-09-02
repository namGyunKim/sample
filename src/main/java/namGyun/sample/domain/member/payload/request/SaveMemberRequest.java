package namGyun.sample.domain.member.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record SaveMemberRequest(
        @NotBlank(message = "로그인 아이디를 입력해주세요.")
        @Schema(description = "로그인 아이디", example = "skarbs01")
        String loginId,
        @NotBlank(message = "이름을 입력해주세요.")
        @Schema(description = "이름", example = "김남균")
        String name,
        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Schema(description = "비밀번호", example = "1234")
        String password
) {
}
