package gyun.sample.domain.board.payload.request;

import gyun.sample.domain.board.enums.BoardType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateBoardRequest(
        @Schema(description = "게시판 아이디", example = "1")
        long boardId,
        @NotNull(message = "제목을 입력해주세요.")
        @NotBlank(message = "제목을 입력해주세요.")
        @Schema(description = "제목", example = "제목수정")
        String title,
        @NotNull(message = "내용을 입력해주세요.")
        @NotBlank(message = "내용을 입력해주세요.")
        @Schema(description = "내용", example = "내용수정")
        String content,
        @Schema(description = "공지 여부", example = "false")
        boolean notice,
        @NotNull(message = "게시판 타입을 입력해주세요.")
        BoardType boardType
) {
}
