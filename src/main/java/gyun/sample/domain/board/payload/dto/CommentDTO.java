package gyun.sample.domain.board.payload.dto;

import gyun.sample.domain.board.entity.BoardComment;
import gyun.sample.domain.board.enums.BoardType;

public record CommentDTO(
        long commentId,
        String content,
        Long parentId,
        long boardId,
        BoardType boardType,
        long memberId,
        String nickName
) {
    public CommentDTO(BoardComment comment) {
        this(comment.getId(), comment.getContent(), comment.getParent() == null ? null : comment.getParent().getId(), comment.getBoard().getId(), comment.getBoard().getBoardType(), comment.getMember().getId(), comment.getMember().getNickName());
    }
}
