package gyun.sample.domain.board.payload.response;

import gyun.sample.domain.board.entity.BoardComment;
import gyun.sample.domain.board.payload.dto.CommentDetailDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MyCommentListResponse {
    private CommentDetailDTO comments;

    public MyCommentListResponse(BoardComment comment) {
        this.comments = new CommentDetailDTO(comment);
    }
}
