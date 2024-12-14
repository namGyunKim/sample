package gyun.sample.domain.board.validator;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.account.payload.response.TokenResponse;
import gyun.sample.domain.board.adapter.ReadBoardServiceAdapter;
import gyun.sample.domain.board.entity.Board;
import gyun.sample.domain.board.enums.BoardType;
import gyun.sample.domain.board.payload.request.BoardInactiveRequest;
import gyun.sample.domain.board.service.read.ReadBoardService;
import gyun.sample.global.utils.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class BoardInactiveValidator implements Validator {

    private final JwtTokenProvider jwtTokenProvider;
    private final HttpServletRequest httpServletRequest;
    private final ReadBoardServiceAdapter readBoardServiceAdapter;

    @Override
    public boolean supports(Class<?> clazz) {
        return BoardInactiveRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        BoardInactiveRequest request = (BoardInactiveRequest) target;
        validateRequest(request, errors);
    }

    private void validateRequest(BoardInactiveRequest request, Errors errors) {
        TokenResponse tokenResponse = jwtTokenProvider.getTokenResponse(httpServletRequest);

        if (isInvalidBoardType(request, errors)) return;

        ReadBoardService readBoardService = readBoardServiceAdapter.getService(request.boardType());
        Board board = readBoardService.getBoardById(request.boardId());
        AccountRole accountRole = AccountRole.getByName(tokenResponse.role());

        if (!hasPermissionToUpdateBoard(board, tokenResponse, accountRole, request.boardType())) {
            errors.rejectValue("boardId", "invalid.boardId", "게시판 수정 권한이 없습니다.");
        }

        if (isAdmin(accountRole) && isInactiveReasonInvalid(request.inactiveReason())) {
            errors.rejectValue("inactiveReason", "invalid.inactiveReason", "관리자의 경우 비활성화 사유를 입력해주세요.");
        }
    }

    private boolean isInvalidBoardType(BoardInactiveRequest request, Errors errors) {
        if (request.boardType() == BoardType.ALL) {
            errors.rejectValue("boardType", "invalid.boardType", "게시판 타입을 선택해주세요.");
            return true;
        }
        return false;
    }

    private boolean hasPermissionToUpdateBoard(Board board, TokenResponse tokenResponse, AccountRole accountRole, BoardType boardType) {
        if (isBoardOwner(board, tokenResponse) || isAdmin(accountRole)) {
            return true;
        }

        return false;
    }

    private boolean isBoardOwner(Board board, TokenResponse tokenResponse) {
        return board.getMember().getId() == tokenResponse.id();
    }

    private boolean isAdmin(AccountRole accountRole) {
        return accountRole == AccountRole.ADMIN || accountRole == AccountRole.SUPER_ADMIN;
    }


    private boolean isInactiveReasonInvalid(String inactiveReason) {
        return inactiveReason == null || inactiveReason.isBlank();
    }
}