package gyun.sample.domain.board.validator;

import gyun.sample.domain.account.dto.CurrentAccountDTO;
import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.account.validator.AccountValidator;
import gyun.sample.domain.account.validator.utils.AccountValidatorUtil;
import gyun.sample.domain.board.payload.request.SaveBoardRequest;
import gyun.sample.domain.board.validator.utils.BoardValidatorUtil;
import gyun.sample.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Component;


@Component
public class BoardValidator extends AccountValidator {

    //    utils
    private final BoardValidatorUtil boardValidatorUtil;

    public BoardValidator(MemberRepository userRepository, MemberRepository userRepository1, AccountValidatorUtil accountValidatorUtil, BoardValidatorUtil boardValidatorUtil) {
        super(userRepository, userRepository1, accountValidatorUtil);
        this.boardValidatorUtil = boardValidatorUtil;
    }

    public void save(SaveBoardRequest request, AccountRole role) {
        boardValidatorUtil.checkGuestWrite(request.boardType(), role);
    }

    public void informationForAdminById(CurrentAccountDTO account) {
        checkAdminRole(account.role());
    }
}
