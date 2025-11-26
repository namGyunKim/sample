package gyun.sample.global.aop;

import gyun.sample.global.exception.BindingException;
import gyun.sample.global.exception.enums.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Aspect
@Component
public class BindingAdvice {

    // RestController에서만 동작하도록 설정
    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object validationHandler(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();

        for (Object arg : args) {
            if (arg instanceof BindingResult bindingResult) {
                if (bindingResult.hasErrors()) {
                    Map<String, String> errorMap = new HashMap<>();
                    for (FieldError error : bindingResult.getFieldErrors()) {
                        errorMap.put(error.getField(), error.getDefaultMessage());
                        log.warn("Validation Error - Field: {}, Message: {}", error.getField(), error.getDefaultMessage());
                    }
                    // RestController에서는 JSON 응답을 위해 예외를 던짐
                    throw new BindingException(ErrorCode.REQUEST_BINDING_RESULT, errorMap.toString());
                }
            }
        }
        return joinPoint.proceed();
    }
}