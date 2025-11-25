package gyun.sample.global.resolver;

import gyun.sample.domain.account.payload.dto.CurrentAccountDTO;
import gyun.sample.global.annotaion.CurrentAccount;
import gyun.sample.global.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class CurrentAccountResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentAccount.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 1. 인증 정보가 없거나, 익명 사용자일 경우 (Guest 처리)
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return CurrentAccountDTO.generatedGuest();
        }

        // 2. PrincipalDetails 객체가 아닌 경우 (다른 인증 메커니즘 사용 시)
        if (!(authentication.getPrincipal() instanceof PrincipalDetails principalDetails)) {
            // Spring Security가 인증했지만 우리가 정의하지 않은 Principal인 경우
            return CurrentAccountDTO.generatedGuest();
        }

        // 3. PrincipalDetails -> CurrentAccountDTO 변환
        return new CurrentAccountDTO(
                principalDetails.getId(),
                principalDetails.getUsername(), // loginId
                principalDetails.getNickName(),
                principalDetails.getRole(),
                principalDetails.getMemberType()
        );
    }
}