package namGyun.sample.global.exception.advice;

import lombok.extern.slf4j.Slf4j;
import namGyun.sample.global.exception.enums.ErrorCode;
import namGyun.sample.global.exception.payload.response.BindingResultResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Aspect
@Component
@Profile("local")
public class BindingAdvice {


    @Around("execution(* namGyun.sample..*Controller.*(..))")
    public Object validationHandler(ProceedingJoinPoint joinPoint) throws Throwable {
        String type = joinPoint.getSignature().getDeclaringTypeName();
        String method = joinPoint.getSignature().getName();
        String errorCode = ErrorCode.REQUEST_BINDING_RESULT.getCode();
        String errorMessage = ErrorCode.REQUEST_BINDING_RESULT.getErrorMessage();
        Map<String, String> errorMap = new HashMap<>();
        Object[] args = joinPoint.getArgs(); // join point parameter
        for (Object arg : args) {
//            바인딩 리절트가 존재하면
            if (arg instanceof BindingResult bindingResult) {
                if (bindingResult.hasErrors()) {
                    populateErrorMap(bindingResult, errorMap);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BindingResultResponse(false,type,method,errorCode,errorMessage,errorMap));
                }
            }
        }
        return joinPoint.proceed();
    }

    private void populateErrorMap(BindingResult bindingResult, Map<String, String> errorMap) {
        for (FieldError error : bindingResult.getFieldErrors()) {
            errorMap.put(error.getField(), error.getDefaultMessage());
        }
    }
}