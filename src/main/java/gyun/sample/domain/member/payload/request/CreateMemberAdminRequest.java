package gyun.sample.domain.member.payload.request;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.enums.MemberType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateMemberAdminRequest(
        @NotBlank(message = "로그인 아이디를 입력해주세요.")
        @Schema(description = "로그인 아이디", example = "admin1")
        String loginId,
        @NotBlank(message = "닉네임을 입력해주세요.")
        @Schema(description = "닉네임", example = "테스트1")
        String nickName,
        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Schema(description = "비밀번호", example = "1234")
        String password,
        @NotNull(message = "권한을 선택해주세요.")
        @Schema(description = "권한", example = "ADMIN")
        AccountRole role,
        @NotNull(message = "멤버 타입을 선택해주세요.")
        @Schema(description = "멤버 타입", example = "GENERAL")
        MemberType memberType) {

    public CreateMemberAdminRequest generatedWithUser() {
        return new CreateMemberAdminRequest(loginId, nickName, password, AccountRole.USER, MemberType.GENERAL);
    }
}
