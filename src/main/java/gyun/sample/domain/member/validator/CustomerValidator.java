package gyun.sample.domain.member.validator;

import gyun.sample.domain.member.payload.request.SaveMemberForCustomerRequest;
import gyun.sample.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Component;


@Component
public class CustomerValidator extends MemberValidator {

    public CustomerValidator(MemberRepository userRepository) {
        super(userRepository);
    }

    public void validateSaveCustomer(SaveMemberForCustomerRequest request){
        existLoginId(request.loginId());
    }

}
