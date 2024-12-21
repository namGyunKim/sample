package gyun.sample.global.resolver;

import gyun.sample.domain.account.payload.dto.CurrentAccountDTO;
import gyun.sample.global.annotaion.CurrentAccount;
import gyun.sample.global.utils.UtilService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class CurrentAccountResolver implements HandlerMethodArgumentResolver {

    private final UtilService utilService;
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // @CurrentAccount가 붙어 있고, 타입이 CurrentAccountDTO인지 확인
        return parameter.hasParameterAnnotation(CurrentAccount.class)
                && parameter.getParameterType().equals(CurrentAccountDTO.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {

        return utilService.getLoginDataOrGuest();
    }
}
