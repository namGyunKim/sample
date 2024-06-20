package gyun.sample.domain.member.payload.request.admin;

import gyun.sample.global.enums.GlobalFilterEnums;
import gyun.sample.global.enums.GlobalOrderEnums;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AllMemberRequest(
        @Schema(description = "페이지 번호", example = "1")
        @Positive(message = "페이지 번호는 1 이상이어야 합니다.")
        int page,
        @Schema(description = "페이지 사이즈", example = "10")
        @Positive(message = "페이지 사이즈는 1 이상이어야 합니다.")
        int size,
        @NotNull(message = "정렬 기준을 선택해주세요.")
        GlobalOrderEnums order,
        @Schema(description = "검색어", example = "관리자")
        String searchWord,
        @NotNull(message = "필터 기준을 선택해주세요.")
        GlobalFilterEnums filter) {
}
