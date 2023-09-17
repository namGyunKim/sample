package gyun.sample.domain.member.payload.request.customer;

import gyun.sample.domain.member.enums.MemberType;
import gyun.sample.domain.searchfilter.enums.ActiveType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SearchCustomerForAdminRequest(
        @Schema(description = "로그인 아이디", example = "skarbs01")
        String loginId,
        @Schema(description = "닉네임", example = "남균찡")
        String nickName,
        @NotNull
        @Schema(description = "회원 타입", example = "ALL")
        MemberType memberType,
        @NotNull
        @Schema(description = "활성화 여부", example = "ALL")
        ActiveType activeType,
        @Positive(message = "page는 1보다 커야합니다.")
        @Schema(description = "페이지", example = "1")
        int page,
        @Positive(message = "size는 1보다 커야합니다.")
        @Schema(description = "사이즈", example = "20")
        int size
) {
}
