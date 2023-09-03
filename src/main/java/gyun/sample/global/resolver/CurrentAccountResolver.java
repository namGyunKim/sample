package gyun.sample.global.resolver;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import gyun.sample.domain.account.dto.CurrentAccountDTO;
import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.global.annotaion.CurrentAccount;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.thymeleaf.util.StringUtils;


// TODO: 2023/09/03 JWT 토큰을 이용한 인증 처리 작업 필요
@Component
@RequiredArgsConstructor
public class CurrentAccountResolver implements HandlerMethodArgumentResolver {
    private static final String BEARER = "Bearer";
    private static final String GUEST = "GUEST";

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
        HttpServletRequest httpServletRequest = (HttpServletRequest) webRequest.getNativeRequest();

        String authorization = httpServletRequest.getHeader("Authorization");
        if(StringUtils.isEmpty(authorization)){
            return new CurrentAccountDTO(GUEST, GUEST, AccountRole.GUEST);
        }
//        Claims claims = jwtUtil.getTokenClaims(authorization.replace(BEARER, "").trim());
//        final String confirmAuthority = claims.get("authority", String.class);
//        final String confirmLoginId = claims.get("loginId", String.class);
//        final String confirmAccountId = claims.get("accountId", String.class);
//        final String confirmUserCode = claims.get("userCode", String.class);
//        AccountAuthority authority = AccountAuthority.valueOf(confirmAuthority);
        return new CurrentAccountDTO(GUEST,GUEST, AccountRole.GUEST);
    }
}
