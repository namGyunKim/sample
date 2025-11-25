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
        AccountLoginRequest request = (AccountLoginRequest) target;
        // Spring Security의 기본 Form Login은 'username'과 'password'만 사용하지만,
        // Thymeleaf 폼 바인딩을 통해 'role'도 함께 받으므로, 이 Validator에서 미리 검증을 수행합니다.
        validateMemberRequest(request, errors);
    }


    /**
     * Spring Security Form Login을 위해 GlobalException 대신 Errors.reject()를 사용하도록 수정
     * 이 Validator는 Spring Security 인증 전에만 호출되도록 의도합니다.
     */
    private void validateMemberRequest(AccountLoginRequest request, Errors errors) {
        // [수정] LoginAccountRequest는 Spring Security가 처리할 때 직접 사용되지 않으므로,
        // 이 Validator는 @InitBinder와 AOP의 조합으로 유효성 검사만 수행해야 합니다.
        // Spring Security의 기본 Form Login 필터는 'username'과 'password'만 사용하며,
        // `role`을 사용하려면 커스텀 필터가 필요합니다.
        // 여기서는 `AccountController::login` 메서드에 `AccountLoginRequest`가 `@ModelAttribute`로 바인딩될 때 이 Validator가 호출된다고 가정하고 진행합니다.

        // Spring Security의 DaoAuthenticationProvider를 존중하여, 비밀번호 검증은 제거합니다.
        // 여기서는 계정 존재 여부와 활성화 상태만 검증합니다.
        // **[중요]** LoginAccountRequest가 `@ModelAttribute("accountLoginRequest")`로 Controller에서 바인딩될 때 Validator가 호출되므로,
        // `login.html`의 폼 필드 이름(`username`, `password`, `role`)이 `AccountLoginRequest` 레코드의 필드 이름과 일치하는지 확인해야 합니다.
        // 하지만 `login.html`은 Spring Security 기본 필드인 `username`과 `password`를 사용하고 있습니다.
        // 이를 해결하기 위해, `AccountController`의 `login` POST 메서드는 `AccountLoginRequest`를 `@ModelAttribute`로 바인딩받고,
        // `login.html`의 필드 이름을 `loginId`로 변경하거나, 커스텀 필터가 필요합니다.

        // **[임시 수정]** `login.html`의 필드 이름을 `loginId`와 `password`로 가정하고, `AccountLoginRequest`의 필드와 일치시킨 후,
        // Spring Security의 기본 `username` 파라미터를 사용하지 않는다고 가정하고 Validator를 실행합니다.
        // (단, Spring Security는 `username`을 사용하므로, `login.html`을 수정해야 합니다.)

        Optional<Member> memberOptional = memberRepository.findByLoginIdAndRole(request.loginId(), request.role());

        if (memberOptional.isEmpty()) {
            // 회원 없음 (Spring Security가 잡을 수 있도록 `errors.reject` 사용)
            errors.reject("login.fail", "아이디 또는 비밀번호가 올바르지 않습니다.");
            return;
        }

        Member member = memberOptional.get();

        // 1. 활성화 상태 체크
        if (member.getActive() != GlobalActiveEnums.ACTIVE) {
            errors.reject("login.fail", "비활성화된 계정입니다.");
        }

        // 비밀번호 체크는 Spring Security의 DaoAuthenticationProvider에 맡깁니다.
        // DaoAuthenticationProvider가 실패하면 `AuthenticationFailureHandler`가 `/account/login?error`로 리다이렉트합니다.
        // 이 Validator가 오류를 발생시키면, Spring Security의 일반적인 인증 흐름(특히 Form Login)이 깨질 수 있습니다.

        // [최종 결정] 이 Validator는 **비밀번호를 제외한** 비즈니스 로직(존재 여부, 활성화)만 검증하며,
        // 실패 시 `errors.reject()`를 사용하여 BindingAdvice를 통해 `BindingException`을 던집니다.
        // `AccountController`에서 이 예외가 발생하면 `ExceptionAdvice`가 잡아 HTML 에러 페이지로 리다이렉트됩니다.
        // Spring Security의 기본 인증 플로우와는 별개로, 폼 데이터를 바인딩할 때의 유효성 검사 계층으로 작동합니다.
        // **단, `LoginAccountValidator`의 `validatePassword` 호출 및 `errors.reject`는 제거합니다.**

        // 비밀번호 검증 로직 제거
    }
}