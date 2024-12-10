package gyun.sample.domain.account.validator;

import gyun.sample.domain.account.payload.request.AccountLoginRequest;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.global.enums.GlobalActiveEnums;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class LoginAccountValidator implements Validator {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean supports(Class<?> clazz) {
        return AccountLoginRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        // CreateMemberRequest 검증
        AccountLoginRequest request = (AccountLoginRequest) target;
        validateMemberRequest(request, errors);
    }


    private void validateMemberRequest(AccountLoginRequest request, Errors errors) {
        Member member = memberRepository.findByLoginIdAndRole(request.loginId(), request.role())
                .orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_EXIST));
        if (member.getActive() != GlobalActiveEnums.ACTIVE) {
            throw new GlobalException(ErrorCode.MEMBER_INACTIVE);
        }
        validatePassword(request.password(), member.getPassword());
    }

    private void validatePassword(String password, String memberPassword) {
        boolean matches = passwordEncoder.matches(password, memberPassword);
        if (!matches) {
            throw new GlobalException(ErrorCode.PASSWORD_NOT_MATCH);
        }
    }
}
