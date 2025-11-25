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
import java.util.stream.Collectors;

/**
 * BindingResult AOP 처리
 * - 컨트롤러 메서드 실행 전 BindingResult를 검사
 * - 에러 발생 시 BindingException 던짐 -> ExceptionAdvice에서 처리
 * - 주의: Thymeleaf 폼 검증 시, 이 방식은 에러 페이지로 이동하게 됩니다.
 * 인라인 에러 메시지를 원한다면 컨트롤러 내부에서 hasErrors()를 직접 체크해야 하지만,
 * 베이스 프로젝트 원칙상 AOP로 일괄 처리합니다.
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
                    Map<String, String> errorMap = bindingResult.getFieldErrors().stream()
                            .collect(Collectors.toMap(
                                    FieldError::getField,
                                    error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid Value",
                                    (existing, replacement) -> existing // 중복 키 발생 시 기존 값 유지
                            ));

                    log.warn("========== Validation Error ==========");
                    errorMap.forEach((field, message) -> log.warn("Field: [{}], Message: [{}]", field, message));
                    log.warn("======================================");

                    // 예외 발생 -> ExceptionAdvice에서 포착
                    throw new BindingException(ErrorCode.REQUEST_BINDING_RESULT, errorMap.toString());
                }
            }
        }
        return joinPoint.proceed();
    }
}