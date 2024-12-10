package gyun.sample.domain.board.payload.request;

import gyun.sample.domain.board.enums.BoardType;
import gyun.sample.global.enums.GlobalActiveEnums;
import gyun.sample.global.enums.GlobalFilterEnums;
import gyun.sample.global.enums.GlobalOrderEnums;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BoardRequestList(
        @Schema(description = "페이지 번호", example = "1")
        @Positive(message = "페이지 번호는 1 이상이어야 합니다.")
        int page,
        @Schema(description = "페이지 사이즈", example = "10")
        @Positive(message = "페이지 사이즈는 1 이상이어야 합니다.")
        int size,
        @NotNull(message = "정렬 기준을 선택해주세요.")
        GlobalOrderEnums order,
        @Schema(description = "검색어", example = "제목")
        String searchWord,
        @NotNull(message = "게시판 종류를 선택해주세요.")
        BoardType boardType,
        @NotNull(message = "필터 기준을 선택해주세요.")
        @Schema(description = "필터 기준", example = "ALL")
        GlobalFilterEnums filter,
        @NotNull(message = "활성화 여부를 선택해주세요.")
        @Schema(description = "활성화 여부", example = "ACTIVE")
        GlobalActiveEnums active) {
}
