package gyun.sample.domain.member.validator;

import lombok.RequiredArgsConstructor;
import gyun.sample.domain.member.payload.request.SaveMemberRequest;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.enums.ErrorCode;
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
