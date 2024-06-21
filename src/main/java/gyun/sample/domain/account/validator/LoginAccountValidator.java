package gyun.sample.domain.account.validator;

import gyun.sample.domain.account.payload.request.AccountLoginRequest;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.global.error.enums.ErrorCode;
import gyun.sample.global.exception.GlobalException;
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
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_EXIST_MEMBER));
        validatePassword(request.password(), member.getPassword());
    }

    private void validatePassword(String password, String memberPassword) {
        boolean matches = passwordEncoder.matches(password, memberPassword);
        if (!matches) {
            throw new GlobalException(ErrorCode.NOT_MATCH_PASSWORD);
        }
    }
}
