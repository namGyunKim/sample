package namGyun.sample.domain.member.validator;

import lombok.RequiredArgsConstructor;
import namGyun.sample.domain.member.payload.request.SaveMemberRequest;
import namGyun.sample.domain.member.repository.MemberRepository;
import namGyun.sample.global.exception.GlobalException;
import namGyun.sample.global.exception.enums.ErrorCode;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class UserValidator {
    private final MemberRepository userRepository;

    public void validateSaveUser(SaveMemberRequest request){
        existLoginId(request.loginId());

    }

    private void existLoginId(String loginId){
        boolean isExist = userRepository.existByLoginId(loginId);
        if (isExist) {
            throw new GlobalException(ErrorCode.ALREADY_REGISTERED_MEMBER_BY_LOGIN_ID);
        }
    }
}
