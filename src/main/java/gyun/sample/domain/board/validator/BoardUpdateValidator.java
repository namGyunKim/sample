package gyun.sample.domain.board.validator;

import gyun.sample.domain.account.payload.response.TokenResponse;
import gyun.sample.domain.board.adapter.ReadBoardServiceAdapter;
import gyun.sample.domain.board.entity.Board;
import gyun.sample.domain.board.enums.BoardType;
import gyun.sample.domain.board.payload.request.BoardUpdateRequest;
import gyun.sample.domain.board.service.read.ReadBoardService;
import gyun.sample.global.utils.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class BoardUpdateValidator implements Validator {

    private final JwtTokenProvider jwtTokenProvider;
    private final HttpServletRequest httpServletRequest;
    private final ReadBoardServiceAdapter readBoardServiceAdapter;

    @Override
    public boolean supports(Class<?> clazz) {
        return BoardUpdateRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        // CreateMemberRequest 검증
        BoardUpdateRequest request = (BoardUpdateRequest) target;
        validateRequest(request, errors);
    }


    // UpdateBoardRequest 검증 메서드입니다.
    private void validateRequest(BoardUpdateRequest request, Errors errors) {
        TokenResponse tokenResponse = jwtTokenProvider.getTokenResponse(httpServletRequest);

        // 게시판 타입이 유효한지 확인합니다.
        if (isInvalidBoardType(request, errors)) return;

        ReadBoardService readBoardService = readBoardServiceAdapter.getService(request.boardType());
        Board board = readBoardService.getBoardById(request.boardId());

        // 게시판 수정 권한이 있는지 확인합니다.
        if (!isBoardOwner(board, tokenResponse)) {
            errors.rejectValue("boardId", "invalid.boardId", "게시판 수정 권한이 없습니다.");
        }
    }

    // 게시판 타입이 유효한지 확인하는 메서드입니다.
    private boolean isInvalidBoardType(BoardUpdateRequest request, Errors errors) {
        if (request.boardType() == BoardType.ALL) {
            errors.rejectValue("boardType", "invalid.boardType", "게시판 타입을 선택해주세요.");
            return true;
        }
        return false;
    }

    // 게시판 소유자인지 확인하는 메서드입니다.
    private boolean isBoardOwner(Board board, TokenResponse tokenResponse) {
        return board.getMember().getId() == tokenResponse.id();
    }
}