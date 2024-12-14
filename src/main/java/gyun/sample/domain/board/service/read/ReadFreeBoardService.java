package gyun.sample.domain.board.service.read;


import gyun.sample.domain.board.entity.Board;
import gyun.sample.domain.board.payload.request.BoardDetailRequest;
import gyun.sample.domain.board.payload.request.BoardListRequest;
import gyun.sample.domain.board.payload.response.BoardDetailResponse;
import gyun.sample.domain.board.payload.response.BoardListResponse;
import gyun.sample.domain.board.repository.BoardRepository;
import gyun.sample.global.enums.GlobalActiveEnums;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.enums.ErrorCode;
import gyun.sample.global.utils.UtilService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReadFreeBoardService implements ReadBoardService {

    private final BoardRepository boardRepository;


    @Override
    public BoardDetailResponse getBoard(BoardDetailRequest boardDetailRequest) {
        Board board = boardRepository.findById(boardDetailRequest.boardId()).orElseThrow(() -> new GlobalException((ErrorCode.BOARD_NOT_EXIST)));
        boardValidate(board);
        return new BoardDetailResponse(board);
    }

    @Override
    public Board getBoardById(long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new GlobalException((ErrorCode.BOARD_NOT_EXIST)));
        boardValidate(board);
        return board;
    }

    @Override
    public Page<BoardListResponse> getBoardList(BoardListRequest request) {
        Pageable pageable = UtilService.getPageable(request.page(), request.size());
        Page<Board> boardList = boardRepository.getBoardList(request, pageable);
        return boardList.map(BoardListResponse::new);
    }

    private void boardValidate(Board board) {
        if (board.getActive() != GlobalActiveEnums.ACTIVE) {
            throw new GlobalException(ErrorCode.BOARD_INACTIVE);
        }
        if (board.getFreeBoard() == null) {
            throw new GlobalException(ErrorCode.FREE_BOARD_NOT_EXIST);
        }
    }
}
