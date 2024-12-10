package gyun.sample.domain.board.payload.request;

import gyun.sample.domain.board.enums.BoardType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record InactiveBoardRequest(
        @Schema(description = "게시판 아이디", example = "1")
        long boardId,
        @Schema(description = "비활성화 사유 nullable", example = "비활성화 사유")
        String inactiveReason,
        @NotNull(message = "게시판 종류를 선택해주세요.")
        BoardType boardType
) {
}
