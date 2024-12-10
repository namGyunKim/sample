package gyun.sample.domain.board.payload.request;

import gyun.sample.domain.board.enums.BoardType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateCommentRequest(
        @Schema(description = "게시판 ID", example = "1")
        @Positive(message = "게시판 ID는 1 이상이어야 합니다.")
        long boardId,
        @NotNull(message = "내용을 입력해주세요.")
        @NotBlank(message = "내용을 입력해주세요.")
        @Schema(description = "내용", example = "댓글내용임.")
        String content,
        @Schema(description = "부모 댓글 ID")
        Long parentId,
        @NotNull(message = "게시판 타입을 선택해주세요.")
        @Schema(description = "게시판 타입", example = "QUESTION")
        BoardType boardType
) {
}
