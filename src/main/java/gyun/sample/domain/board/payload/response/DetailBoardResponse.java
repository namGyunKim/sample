package gyun.sample.domain.board.payload.response;

import gyun.sample.domain.board.entity.Board;
import gyun.sample.domain.board.payload.dto.CommentDTO;
import gyun.sample.domain.board.payload.dto.DetailBoardDTO;
import gyun.sample.domain.member.payload.dto.DetailMemberProfileDTO;
import gyun.sample.global.enums.GlobalActiveEnums;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class DetailBoardResponse {
    private DetailBoardDTO board;
    private DetailMemberProfileDTO profile;
    private List<CommentDTO> comments;

    public DetailBoardResponse(Board board) {
        this.board = new DetailBoardDTO(board);
        this.profile = new DetailMemberProfileDTO(board.getMember());
        this.comments = board.getComments().stream().filter(comment -> comment.getActive() == GlobalActiveEnums.ACTIVE).map(CommentDTO::new).toList();
    }
}
