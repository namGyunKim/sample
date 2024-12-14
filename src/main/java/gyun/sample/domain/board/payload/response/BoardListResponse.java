package gyun.sample.domain.board.payload.response;

import gyun.sample.domain.board.entity.Board;
import gyun.sample.domain.board.payload.dto.BoardDetailDTO;
import gyun.sample.domain.board.payload.dto.CommentDTO;
import gyun.sample.domain.member.payload.dto.MemberProfileDetailDTO;
import gyun.sample.global.enums.GlobalActiveEnums;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class BoardListResponse {
    private BoardDetailDTO board;
    private MemberProfileDetailDTO profile;
    private List<CommentDTO> comments;

    public BoardListResponse(Board board) {
        this.board = new BoardDetailDTO(board);
        this.profile = new MemberProfileDetailDTO(board.getMember());
        this.comments = board.getComments().stream().filter(comment -> comment.getActive() == GlobalActiveEnums.ACTIVE).map(CommentDTO::new).toList();
    }
}
