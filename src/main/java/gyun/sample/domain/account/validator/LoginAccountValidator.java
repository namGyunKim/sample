package gyun.sample.domain.account.validator;

import gyun.sample.domain.account.payload.request.AccountLoginRequest;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.global.enums.GlobalActiveEnums;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

/**
 * 로그인 요청 DTO (AccountLoginRequest) 유효성 검증
 * Spring Security의 기본 인증 필터 앞단이나, 커스텀 로그인 컨트롤러 진입 시 사용됩니다.
 * 비밀번호 검증은 Security의 AuthenticationProvider에 위임하고, 여기서는 계정 존재 여부와 상태를 확인합니다.
 */
@Component
@RequiredArgsConstructor
public class LoginAccountValidator implements Validator {

    private final MemberRepository memberRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return AccountLoginRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AccountLoginRequest request = (AccountLoginRequest) target;
        validateMemberRequest(request, errors);
    }

    private void validateMemberRequest(AccountLoginRequest request, Errors errors) {
        // 1. 계정 조회 (아이디 + 권한)
        Optional<Member> memberOptional = memberRepository.findByLoginIdAndRole(request.loginId(), request.role());

        if (memberOptional.isEmpty()) {
            errors.reject("login.fail", "아이디가 존재하지 않거나 권한이 일치하지 않습니다.");
            return;
        }

        Member member = memberOptional.get();

        // 2. 활성화 상태 체크
        if (member.getActive() != GlobalActiveEnums.ACTIVE) {
            errors.reject("login.fail", "비활성화된 계정입니다. 관리자에게 문의하세요.");
        }

        // 비밀번호 검증은 Spring Security의 DaoAuthenticationProvider에서 수행하므로 생략합니다.
    }
}