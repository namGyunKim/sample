package gyun.sample.domain.member.validator;

import gyun.sample.domain.account.dto.CurrentAccountDTO;
import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.account.validator.AccountValidator;
import gyun.sample.domain.account.validator.utils.AccountValidatorUtil;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.payload.request.SaveMemberForCustomerRequest;
import gyun.sample.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Component;


@Component
public class CustomerValidator extends AccountValidator {

    public CustomerValidator(MemberRepository userRepository, MemberRepository userRepository1, AccountValidatorUtil accountValidatorUtil) {
        super(userRepository, userRepository1, accountValidatorUtil);
    }

    //    고객 회원가입
    public void validateSaveCustomer(SaveMemberForCustomerRequest request){
        notExistByLoginId(request.loginId());
    }

    public void informationCustomerForAdmin(CurrentAccountDTO account, Member member) {
        checkRole(account.role(), AccountRole.ADMIN);
        checkTargetRole(member,AccountRole.CUSTOMER);
    }
}
