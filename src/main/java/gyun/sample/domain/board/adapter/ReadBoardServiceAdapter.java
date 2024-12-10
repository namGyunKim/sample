package gyun.sample.domain.board.adapter;

import gyun.sample.domain.board.enums.BoardType;
import gyun.sample.domain.board.service.read.ReadBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReadBoardServiceAdapter {

    private final ReadBoardService readFreeBoardService;
    private final ReadBoardService readQuestionBoardService;


    public ReadBoardService getService(BoardType boardType) {
        return switch (boardType) {
            case FREE -> readFreeBoardService;
            case QUESTION -> readQuestionBoardService;
            default -> throw new IllegalArgumentException("지원하지 않는 게시판 엔티티 타입입니다: " + boardType);
        };
    }
}