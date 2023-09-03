package gyun.sample.domain.member.validator;

import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class MemberValidator {
    private final MemberRepository userRepository;

    protected void existLoginId(String loginId){
        boolean isExist = userRepository.existByLoginId(loginId);
        if (isExist) {
            throw new GlobalException(ErrorCode.ALREADY_REGISTERED_MEMBER_BY_LOGIN_ID);
        }
    }
}
