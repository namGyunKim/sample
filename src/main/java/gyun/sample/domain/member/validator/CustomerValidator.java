package gyun.sample.domain.member.validator;

import gyun.sample.domain.account.validator.AccountValidator;
import gyun.sample.domain.account.validator.utils.AccountValidatorUtil;
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
        existByLoginIdAndActiveAll(request.loginId());
    }

}
