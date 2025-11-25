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

/**
 * REST API 요청에 대한 유효성 검사 결과(BindingResult)를 가로채는 Aspect
 * - @RestController가 붙은 클래스에만 적용됩니다.
 * - 일반 @Controller(View 반환)는 이 로직을 타지 않고 직접 BindingResult를 핸들링합니다.
 */
@Slf4j
@Aspect
@Component
public class BindingAdvice {

    // 포인트컷: @RestController 어노테이션이 붙은 클래스의 모든 메서드
    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object validationHandler(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();

        for (Object arg : args) {
            if (arg instanceof BindingResult bindingResult) {
                // 에러가 존재하면 즉시 예외 발생 -> ExceptionAdvice에서 JSON 응답 처리
                if (bindingResult.hasErrors()) {
                    Map<String, String> errorMap = new HashMap<>();

                    for (FieldError error : bindingResult.getFieldErrors()) {
                        errorMap.put(error.getField(), error.getDefaultMessage());
                        log.warn("API Validation Error - Field: [{}], Message: [{}]", error.getField(), error.getDefaultMessage());
                    }

                    throw new BindingException(ErrorCode.REQUEST_BINDING_RESULT, errorMap.toString());
                }
            }
        }
        return joinPoint.proceed();
    }
}