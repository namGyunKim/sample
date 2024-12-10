package gyun.sample.domain.board.service.read;

import gyun.sample.domain.board.entity.Board;
import gyun.sample.domain.board.payload.request.BoardRequestList;
import gyun.sample.domain.board.payload.request.DetailBoardRequest;
import gyun.sample.domain.board.payload.response.BoardResponseList;
import gyun.sample.domain.board.payload.response.DetailBoardResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface ReadBoardService {

    DetailBoardResponse getBoard(DetailBoardRequest detailBoardRequest);

    Board getBoardById(long boardId);

    Page<BoardResponseList> getBoardList(BoardRequestList boardRequestList);
}
