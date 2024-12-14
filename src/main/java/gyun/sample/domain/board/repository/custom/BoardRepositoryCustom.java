package gyun.sample.domain.board.repository.custom;

import gyun.sample.domain.board.entity.Board;
import gyun.sample.domain.board.payload.request.BoardListRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardRepositoryCustom {


    Page<Board> getBoardList(BoardListRequest request, Pageable pageable);
}
