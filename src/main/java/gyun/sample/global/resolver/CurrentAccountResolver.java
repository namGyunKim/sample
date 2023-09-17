package gyun.sample.global.resolver;

import gyun.sample.domain.account.dto.CurrentAccountDTO;
import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.account.payload.response.TokenResponse;
import gyun.sample.domain.member.enums.MemberType;
import gyun.sample.global.annotaion.CurrentAccount;
import gyun.sample.global.utils.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.thymeleaf.util.StringUtils;


// 컨트롤러 메서드의 파라메터를 바인딩하는 역할
@Component
@RequiredArgsConstructor
public class CurrentAccountResolver implements HandlerMethodArgumentResolver {

    private final JwtTokenProvider jwtTokenProvider;

//    private final JwtUtil jwtUtil;

//    파라메터 객체의 타입이 CurrentAccount인 경우 true를 반환하고 resolveArgument() 메서드가 실행됨
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentAccount.class);
    }

//    resolveArgument() 메서드에서는 HttpServletRequest 객체를 통해 Authorization 헤더를 가져옴

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        final String GUEST = "GUEST";
        HttpServletRequest httpServletRequest = (HttpServletRequest) webRequest.getNativeRequest();

        String authorization= httpServletRequest.getHeader("Authorization");
        String bearer = "";
        if(StringUtils.isEmpty(authorization)){
            return new CurrentAccountDTO(GUEST, GUEST, AccountRole.GUEST, MemberType.GENERAL);
        }else{
            bearer = authorization.split(" ")[1];
            TokenResponse tokenResponse = jwtTokenProvider.getTokenResponse(bearer);
            return new CurrentAccountDTO(tokenResponse);
        }
    }
}
