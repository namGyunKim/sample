package gyun.sample.domain.member.validator;

import gyun.sample.domain.account.dto.CurrentAccountDTO;
import gyun.sample.domain.account.validator.AccountValidator;
import gyun.sample.domain.account.validator.utils.AccountValidatorUtil;
import gyun.sample.domain.member.payload.request.customer.SaveMemberForCustomerRequest;
import gyun.sample.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Component;


@Component
public class CustomerValidator extends AccountValidator {

    public CustomerValidator(MemberRepository userRepository, MemberRepository userRepository1, AccountValidatorUtil accountValidatorUtil) {
        super(userRepository, userRepository1, accountValidatorUtil);
    }

    //    고객 회원가입
    public void validateSaveCustomer(SaveMemberForCustomerRequest request) {
        notExistByLoginId(request.loginId());
    }

    //  관리자 전용 고객 조회
    public void informationCustomerForAdmin(CurrentAccountDTO account) {
        checkAdminRole(account.role());
    }

    //  고객이 자기 자신의 정보 고객 조회
    public void informationCustomerForSelf(CurrentAccountDTO account) {
        checkCustomerRole(account.role());
    }
}
