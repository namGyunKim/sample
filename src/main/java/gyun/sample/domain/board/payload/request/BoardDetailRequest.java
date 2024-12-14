package gyun.sample.domain.board.payload.request;

import gyun.sample.domain.board.enums.BoardType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record BoardDetailRequest(
        @Schema(description = "게시판 ID", example = "1")
        long boardId,
        @NotNull(message = "게시판 타입을 선택해주세요")
        BoardType boardType
) {
}
