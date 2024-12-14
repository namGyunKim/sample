package gyun.sample.domain.board.service.read;

import gyun.sample.domain.board.entity.Board;
import gyun.sample.domain.board.payload.request.BoardDetailRequest;
import gyun.sample.domain.board.payload.request.BoardListRequest;
import gyun.sample.domain.board.payload.response.BoardDetailResponse;
import gyun.sample.domain.board.payload.response.BoardListResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface ReadBoardService {

    BoardDetailResponse getBoard(BoardDetailRequest boardDetailRequest);

    Board getBoardById(long boardId);

    Page<BoardListResponse> getBoardList(BoardListRequest boardListRequest);
}
