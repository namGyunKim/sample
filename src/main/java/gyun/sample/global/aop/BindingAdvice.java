package gyun.sample.global.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import gyun.sample.domain.account.payload.dto.CurrentAccountDTO;
import gyun.sample.global.api.RestApiController;
import gyun.sample.global.exception.advice.RestApiControllerAdvice;
import gyun.sample.global.exception.enums.ErrorCode;
import gyun.sample.global.exception.payload.response.BindingResultResponse;
import gyun.sample.global.utils.UtilService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;


// 바인딩 리절트 에러 핸들러
@Slf4j
@Aspect
@Component
public class BindingAdvice extends RestApiControllerAdvice {

    private final RestApiController restApiController;
    private final UtilService utilService;

    public BindingAdvice(ObjectMapper objectMapper, ApplicationEventPublisher applicationEventPublisher, RestApiController restApiController, UtilService utilService) {
        super(objectMapper, applicationEventPublisher);
        this.restApiController = restApiController;
        this.utilService = utilService;
    }

    @Around("execution(* gyun.sample..*Controller.*(..))")
    public Object validationHandler(ProceedingJoinPoint joinPoint) throws Throwable {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        // RequestAttributes가 null인 경우 WebSocket에서 발생한 요청임으로 무시
        if (requestAttributes == null) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        String type = bindingPathCreate(joinPoint.getSignature());
        String method = joinPoint.getSignature().getName();
        String errorCode = ErrorCode.REQUEST_BINDING_RESULT.getCode();
        String errorMessage = ErrorCode.REQUEST_BINDING_RESULT.getErrorMessage();
        Map<String, String> errorMap = new HashMap<>();
        Object[] args = joinPoint.getArgs(); // join point parameter

        CurrentAccountDTO currentAccountDTO = utilService.getLoginDataOrGuest();
        for (Object arg : args) {
            // 바인딩 리절트가 존재하면
            if (arg instanceof BindingResult bindingResult) {
                if (bindingResult.hasErrors()) {
                    populateErrorMap(bindingResult, errorMap);
                    BindingResultResponse response = new BindingResultResponse(type, method, errorCode, errorMessage, errorMap);
                    sendLogEvent(response, currentAccountDTO, request);
                    return restApiController.createFailRestResponse(response);
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

    private String bindingPathCreate(Signature signature) {
        String declaringTypeName = signature.getDeclaringTypeName();
        String name = signature.getName();
        String[] splitPath = declaringTypeName.split("\\.");
        return splitPath[splitPath.length - 1] + " " + name;
    }
}