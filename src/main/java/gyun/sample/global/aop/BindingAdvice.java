package gyun.sample.global.aop;

import gyun.sample.global.exception.BindingException;
import gyun.sample.global.exception.enums.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

/**
 * BindingResult AOP 처리
 * RestController에서는 예외를 던져 JSON 응답을 내리고,
 * 일반 Controller(View 반환)에서는 컨트롤러 내부에서 hasErrors()를 직접 처리하도록 예외 발생을 건너뜁니다.
 */
@Slf4j
@Aspect
@Component
public class BindingAdvice {

    @Around("execution(* gyun.sample..*Controller.*(..))")
    public Object validationHandler(ProceedingJoinPoint joinPoint) throws Throwable {

        // 반환 타입이 String(View Name)이거나 ModelAndView인 경우,
        // AOP에서 예외를 던지면 입력 폼으로 돌아가지 못하고 에러 페이지로 가버립니다.
        // 따라서 이런 경우에는 AOP 검증을 스킵하고 컨트롤러에게 처리를 위임합니다.
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Class<?> returnType = signature.getReturnType();

        if (returnType.equals(String.class) || returnType.equals(org.springframework.web.servlet.ModelAndView.class)) {
            return joinPoint.proceed();
        }

        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof BindingResult bindingResult) {
                if (bindingResult.hasErrors()) {
                    Map<String, String> errorMap = new HashMap<>();
                    for (FieldError error : bindingResult.getFieldErrors()) {
                        errorMap.put(error.getField(), error.getDefaultMessage());
                    }
                    log.warn("Validation Error (API): {}", errorMap);
                    throw new BindingException(ErrorCode.REQUEST_BINDING_RESULT, errorMap.toString());
                }
            }
        }
        return joinPoint.proceed();
    }
}