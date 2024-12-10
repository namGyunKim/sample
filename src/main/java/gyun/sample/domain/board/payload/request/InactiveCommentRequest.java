package gyun.sample.domain.board.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record InactiveCommentRequest(
        @Schema(description = "댓글 ID", example = "1")
        long commentId,
        @Schema(description = "비활성화 사유", example = "욕설")
        String inactiveReason
) {
}
