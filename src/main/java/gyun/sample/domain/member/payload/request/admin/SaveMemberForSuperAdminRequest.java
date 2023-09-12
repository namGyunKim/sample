package gyun.sample.domain.member.payload.request.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record SaveMemberForSuperAdminRequest(
        @NotBlank(message = "로그인 아이디를 입력해주세요.")
        @Schema(description = "로그인 아이디", example = "superAdmin")
        String loginId,
        @NotBlank(message = "닉네임을 입력해주세요.")
        @Schema(description = "닉네임", example = "남균찡")
        String nickName,
        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Schema(description = "비밀번호", example = "1234!@#Abcd")
        String password
) {
}
