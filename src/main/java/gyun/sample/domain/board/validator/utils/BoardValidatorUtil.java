package gyun.sample.domain.board.validator.utils;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.board.enums.BoardType;
import gyun.sample.global.error.enums.ErrorCode;
import gyun.sample.global.exception.GlobalException;
import org.springframework.stereotype.Component;


@Component
public class BoardValidatorUtil {

    public void checkGuestWrite(BoardType boardType, AccountRole role) {
        if (role.equals(AccountRole.GUEST) && !boardType.equals(BoardType.ANONYMOUS)) {
            throw new GlobalException(ErrorCode.GUEST_NOT_WRITE);
        }

    }

}
