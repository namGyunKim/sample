package gyun.sample.domain.account.validator;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.global.error.enums.ErrorCode;
import gyun.sample.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class AccountValidator {
    protected final MemberRepository userRepository;

    public void validateLogin(Member member,String password){
    passwordCheck(member, password);
    }

    protected void passwordCheck(Member member, String password){
        if(!BCrypt.checkpw(password, member.getPassword())){
            throw new GlobalException(ErrorCode.NOT_MATCH_PASSWORD);
        }
    }

    protected void existLoginIdAndActiveAll(String loginId){
        boolean isExist = userRepository.existByLoginIdAndActiveAll(loginId);
        if (isExist) {
            throw new GlobalException(ErrorCode.ALREADY_REGISTERED_MEMBER_BY_LOGIN_ID);
        }
    }

    protected void existLoginIdAndRole(String loginId, AccountRole role){
        boolean isExist = userRepository.existByLoginIdAndRole(loginId,role);
        if (isExist) {
            throw new GlobalException(ErrorCode.ALREADY_REGISTERED_MEMBER_BY_LOGIN_ID);
        }
    }
}
