package gyun.sample.global.resolver;

import gyun.sample.domain.account.payload.dto.CurrentAccountDTO;
import gyun.sample.global.annotaion.CurrentAccount;
import gyun.sample.global.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
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

        // 인증 정보가 없거나, 익명 사용자일 경우 (Guest 처리)
        if (authentication == null || authentication.getPrincipal().equals("anonymousUser")) {
            return CurrentAccountDTO.generatedGuest();
        }

        // JwtAuthenticationFilter에서 저장한 PrincipalDetails 꺼내기
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

        // PrincipalDetails -> CurrentAccountDTO 변환
        return new CurrentAccountDTO(
                principalDetails.getId(),
                principalDetails.getUsername(), // loginId
                principalDetails.getNickName(),
                principalDetails.getRole(),
                principalDetails.getMemberType()
        );
    }
}