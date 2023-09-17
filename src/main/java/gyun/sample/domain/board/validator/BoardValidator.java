package gyun.sample.domain.board.validator;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.board.payload.request.SaveBoardRequest;
import gyun.sample.domain.board.repository.BoardRepository;
import gyun.sample.domain.board.validator.utils.BoardValidatorUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
public class BoardValidator {

    //    repository
    private final BoardRepository boardRepository;
    //    utils
    private final BoardValidatorUtil boardValidatorUtil;


    public void save(SaveBoardRequest request, AccountRole role) {
        boardValidatorUtil.checkGuestWrite(request.boardType(), role);
    }
}
