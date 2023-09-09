package gyun.sample.domain.member.validator;

import gyun.sample.domain.account.dto.CurrentAccountDTO;
import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.account.validator.AccountValidator;
import gyun.sample.domain.account.validator.utils.AccountValidatorUtil;
import gyun.sample.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Component;


@Component
public class AdminValidator extends AccountValidator {

    public AdminValidator(MemberRepository userRepository, MemberRepository userRepository1, AccountValidatorUtil accountValidatorUtil) {
        super(userRepository, userRepository1, accountValidatorUtil);
    }

    //    고객 조회는 관리자 이상의 권한이 필요합니다.
    public void informationCustomerForAdmin(CurrentAccountDTO account) {
        checkRole(account, AccountRole.ADMIN);
    }
}
