package gyun.sample.domain.board.payload.dto;

import gyun.sample.domain.board.entity.Board;
import gyun.sample.domain.board.enums.BoardType;
import gyun.sample.global.enums.GlobalActiveEnums;

public record BoardDetailDTO(
        long boardId,
        String title,
        String content,
        long viewCount,
        long likeCount,
        boolean notice,
        BoardType boardType,
        GlobalActiveEnums active
) {
    public BoardDetailDTO(Board board) {
        this(board.getId(), board.getTitle(), board.getContent(), board.getViewCount(), board.getLikeCount(), board.isNotice(), board.getBoardType(), board.getActive());
    }
}
