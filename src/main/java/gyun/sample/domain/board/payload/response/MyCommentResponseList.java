package gyun.sample.domain.board.payload.response;

import gyun.sample.domain.board.entity.BoardComment;
import gyun.sample.domain.board.payload.dto.DetailCommentDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MyCommentResponseList {
    private DetailCommentDTO comments;

    public MyCommentResponseList(BoardComment comment) {
        this.comments = new DetailCommentDTO(comment);
    }
}
