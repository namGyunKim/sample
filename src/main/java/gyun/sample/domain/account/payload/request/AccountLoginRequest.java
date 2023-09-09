package gyun.sample.domain.account.payload.request;

import gyun.sample.domain.account.enums.AccountRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AccountLoginRequest(
        @NotBlank(message = "로그인 아이디를 입력해주세요.")
        @Schema(description = "로그인 아이디", example = "superAdmin")
        String loginId,                 //로그인 아이디
        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Schema(description = "비밀번호", example = "1234")
        @Size(min = 4, max = 20, message = "비밀번호는 4자 이상 20자 이하로 입력해주세요.")
        String password,                //비밀번호
        @NotNull(message = "권한을 입력해주세요.")
        AccountRole role                //권한
) {
}
