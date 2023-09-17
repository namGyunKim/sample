package gyun.sample.domain.board.payload.response;

import gyun.sample.domain.board.entity.Board;

public record SaveBoardResponse(
        String id,
        String title,
        String content,
        String boardType
) {

    public SaveBoardResponse(Board board){
        this(board.getId(), board.getTitle(), board.getContent(), board.getBoardType().toString());
    }
}
