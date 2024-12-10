package gyun.sample.domain.board.adapter;

import gyun.sample.domain.board.enums.BoardType;
import gyun.sample.domain.board.service.write.WriteBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WriteBoardServiceAdapter {

    private final WriteBoardService writeFreeBoardService;
    private final WriteBoardService writeQuestionBoardService;


    public WriteBoardService getService(BoardType boardType) {
        return switch (boardType) {
            case FREE -> writeFreeBoardService;
            case QUESTION -> writeQuestionBoardService;
            default -> throw new IllegalArgumentException("지원하지 않는 게시판 엔티티 타입입니다: " + boardType);
        };
    }
}