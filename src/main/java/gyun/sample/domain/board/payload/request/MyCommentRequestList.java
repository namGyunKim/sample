package gyun.sample.domain.board.payload.request;

import gyun.sample.global.enums.GlobalActiveEnums;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

public record MyCommentRequestList(
        @Schema(description = "활성화 상태", example = "ALL")
        GlobalActiveEnums active,
        @Schema(description = "페이지 번호", example = "0")
        @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.")
        int page,
        @Positive(message = "페이지 사이즈는 1 이상이어야 합니다.")
        @Schema(description = "페이지 사이즈", example = "10")
        int size
) {
}
