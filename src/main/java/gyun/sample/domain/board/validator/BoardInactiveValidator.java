package gyun.sample.domain.board.validator;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.account.payload.dto.CurrentAccountDTO;
import gyun.sample.domain.board.adapter.ReadBoardServiceAdapter;
import gyun.sample.domain.board.entity.Board;
import gyun.sample.domain.board.enums.BoardType;
import gyun.sample.domain.board.payload.request.BoardInactiveRequest;
import gyun.sample.domain.board.service.read.ReadBoardService;
import gyun.sample.global.utils.UtilService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class BoardInactiveValidator implements Validator {

    private final ReadBoardServiceAdapter readBoardServiceAdapter;
    private final UtilService utilService;

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
        CurrentAccountDTO currentAccount = utilService.getCurrentAccount();

        if (isInvalidBoardType(request, errors)) return;

        ReadBoardService readBoardService = readBoardServiceAdapter.getService(request.boardType());
        Board board = readBoardService.getBoardById(request.boardId());
        AccountRole accountRole = currentAccount.role();

        if (!hasPermissionToUpdateBoard(board, currentAccount, accountRole, request.boardType())) {
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

    private boolean hasPermissionToUpdateBoard(Board board, CurrentAccountDTO currentAccountDTO, AccountRole accountRole, BoardType boardType) {
        if (isBoardOwner(board, currentAccountDTO) || isAdmin(accountRole)) {
            return true;
        }

        return false;
    }

    private boolean isBoardOwner(Board board, CurrentAccountDTO currentAccountDTO) {
        return board.getMember().getId() == currentAccountDTO.id();
    }

    private boolean isAdmin(AccountRole accountRole) {
        return accountRole == AccountRole.ADMIN || accountRole == AccountRole.SUPER_ADMIN;
    }


    private boolean isInactiveReasonInvalid(String inactiveReason) {
        return inactiveReason == null || inactiveReason.isBlank();
    }
}