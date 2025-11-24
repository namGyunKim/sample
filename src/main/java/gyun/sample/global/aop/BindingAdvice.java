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
 * BindingResult AOP 처리
 * - 컨트롤러 메서드 실행 전 BindingResult를 검사
 * - 에러 발생 시 BindingException 던짐 -> ExceptionAdvice에서 처리
 * - 컨트롤러 메서드 파라미터에 BindingResult가 반드시 포함되어 있어야 함
 */
@Slf4j
@Aspect
@Component
public class BindingAdvice {

    @Around("execution(* gyun.sample..*Controller.*(..))")
    public Object validationHandler(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();

        for (Object arg : args) {
            if (arg instanceof BindingResult bindingResult) {
                if (bindingResult.hasErrors()) {
                    // 에러 맵 생성
                    Map<String, String> errorMap = new HashMap<>();
                    for (FieldError error : bindingResult.getFieldErrors()) {
                        errorMap.put(error.getField(), error.getDefaultMessage());
                    }

                    // 에러 로깅
                    log.warn("Validation Error: {}", errorMap);

                    // 예외 발생 -> RestApiControllerAdvice 등에서 잡아서 응답 처리
                    throw new BindingException(ErrorCode.REQUEST_BINDING_RESULT, errorMap.toString());
                }
            }
        }
        return joinPoint.proceed();
    }
}