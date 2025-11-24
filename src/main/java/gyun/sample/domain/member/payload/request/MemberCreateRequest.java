package gyun.sample.domain.member.payload.request;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.enums.MemberType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MemberCreateRequest(
        @NotBlank(message = "로그인 아이디를 입력해주세요.")
        @Schema(description = "로그인 아이디", example = "user1")
        String loginId,

        @NotBlank(message = "닉네임을 입력해주세요.")
        @Schema(description = "닉네임", example = "테스트1")
        String nickName,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Schema(description = "비밀번호", example = "1234")
        String password,

        @Schema(description = "권한 (관리자 생성 시 필요, 사용자 생성 시 무시됨)", example = "USER")
        AccountRole role,

        @NotNull(message = "멤버 타입을 선택해주세요.")
        @Schema(description = "멤버 타입", example = "GENERAL")
        MemberType memberType
) {
    public static MemberCreateRequest fromUser(String loginId, String nickName, String password, MemberType memberType) {
        return new MemberCreateRequest(loginId, nickName, password, AccountRole.USER, memberType);
    }
}