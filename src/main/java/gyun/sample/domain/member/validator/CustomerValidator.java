package gyun.sample.domain.member.validator;

import gyun.sample.domain.account.validator.AccountValidator;
import gyun.sample.domain.member.payload.request.SaveMemberForCustomerRequest;
import gyun.sample.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Component;


@Component
public class CustomerValidator extends AccountValidator {

    public CustomerValidator(MemberRepository userRepository) {
        super(userRepository);
    }

    public void validateSaveCustomer(SaveMemberForCustomerRequest request){
        existByLoginIdAndActiveAll(request.loginId());
    }

}
