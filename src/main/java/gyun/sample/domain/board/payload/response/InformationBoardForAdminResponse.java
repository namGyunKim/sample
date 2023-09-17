package gyun.sample.domain.board.payload.response;

import gyun.sample.domain.board.entity.Board;

public record InformationBoardForAdminResponse(
        String id,
        String title,
        String content,
        String writer,
        String createdAt,
        String updatedAt) {
    public InformationBoardForAdminResponse(Board board) {
        this(board.getId(), board.getTitle(), board.getContent(), board.getMember().getNickName(), board.getCreatedAt().toString().split("T")[0], board.getModifiedAt().toString().split("T")[0]);
    }
}
