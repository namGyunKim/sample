package gyun.sample.domain.member.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UpdateMemberRequest(
        @NotBlank(message = "닉네임을 입력해주세요.")
        @Schema(description = "닉네임", example = "닉네임변경1")
        String nickName,
        @Schema(description = "비밀번호", example = "1234")
        String password) {
}
