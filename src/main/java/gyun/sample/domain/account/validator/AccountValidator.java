package gyun.sample.domain.account.validator;

import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class AccountValidator {
    private final MemberRepository userRepository;

    public void validateLogin(Member member,String password){
    passwordCheck(member, password);
    }

    protected void passwordCheck(Member member, String password){
        if(!BCrypt.checkpw(password, member.getPassword())){
            throw new GlobalException(ErrorCode.NOT_MATCH_PASSWORD);
        }
    }
}
