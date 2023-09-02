package namGyun.sample.domain.member.validator;

import lombok.RequiredArgsConstructor;
import namGyun.sample.domain.member.payload.request.SaveMemberRequest;
import namGyun.sample.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class UserValidator {
    private final MemberRepository userRepository;

    public void validateSaveUser(SaveMemberRequest request){
        validateLoginId(request.loginId());

    }

    private void validateLoginId(String loginId){
        boolean isExist = userRepository.existByLoginId(loginId);
        System.out.println("isExist = " + isExist);
    }
}
