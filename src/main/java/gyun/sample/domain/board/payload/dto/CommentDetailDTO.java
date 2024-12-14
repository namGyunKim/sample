package gyun.sample.domain.board.payload.dto;

import gyun.sample.domain.board.entity.BoardComment;
import gyun.sample.domain.board.enums.BoardType;

import java.time.LocalDateTime;

public record CommentDetailDTO(
        long commentId,
        String content,
        Long parentId,
        long boardId,
        BoardType boardType,
        long memberId,
        String nickName,
        String createIp,
        String inactiveIp,
        String inactiveReason,
        String inactiveNickName,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {
    public CommentDetailDTO(BoardComment comment) {
        this(comment.getId(),
                comment.getContent(),
                comment.getParent() == null ? null : comment.getParent().getId(),
                comment.getBoard().getId(),
                comment.getBoard().getBoardType(),
                comment.getMember().getId(),
                comment.getMember().getNickName(),
                comment.getCreateIp(),
                comment.getInactiveIp(),
                comment.getInactiveReason(),
                comment.getDeActiveMember() == null ? null : comment.getDeActiveMember().getNickName(),
                comment.getCreatedAt(),
                comment.getModifiedAt());
    }

}
