package gyun.sample.domain.member.validator;

import gyun.sample.domain.account.validator.AccountValidator;
import gyun.sample.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Component;


@Component
public class AdminValidator extends AccountValidator {

    public AdminValidator(MemberRepository userRepository) {
        super(userRepository);
    }
}
