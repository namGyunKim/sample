package gyun.sample.global.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import gyun.sample.domain.account.payload.dto.CurrentAccountDTO;
import gyun.sample.domain.account.payload.response.TokenResponse;
import gyun.sample.global.api.RestApiController;
import gyun.sample.global.exception.advice.RestApiControllerAdvice;
import gyun.sample.global.exception.enums.ErrorCode;
import gyun.sample.global.exception.payload.response.BindingResultResponse;
import gyun.sample.global.utils.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
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

    private final JwtTokenProvider jwtTokenProvider;
    private final RestApiController restApiController;

    public BindingAdvice(ObjectMapper objectMapper, ApplicationEventPublisher applicationEventPublisher, JwtTokenProvider jwtTokenProvider, RestApiController restApiController) {
        super(objectMapper, applicationEventPublisher);
        this.jwtTokenProvider = jwtTokenProvider;
        this.restApiController = restApiController;
    }

    @Around("execution(* gyun.sample..*Controller.*(..))")
    public Object validationHandler(ProceedingJoinPoint joinPoint) throws Throwable {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        // RequestAttributes가 null인 경우 WebSocket 등에서 발생한 요청임으로 무시
        if (requestAttributes == null) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        Object[] args = joinPoint.getArgs(); // join point parameter

        for (Object arg : args) {
            // 바인딩 리절트가 존재하면
            if (arg instanceof BindingResult bindingResult) {
                if (bindingResult.hasErrors()) {
                    String type = bindingPathCreate(joinPoint.getSignature());
                    String method = joinPoint.getSignature().getName();
                    String errorCode = ErrorCode.REQUEST_BINDING_RESULT.getCode();
                    String errorMessage = ErrorCode.REQUEST_BINDING_RESULT.getErrorMessage();
                    Map<String, String> errorMap = new HashMap<>();

                    CurrentAccountDTO currentAccountDTO = getTokenResponse(request);

                    populateErrorMap(bindingResult, errorMap);
                    BindingResultResponse response = new BindingResultResponse(type, method, errorCode, errorMessage, errorMap);

                    // 로그 이벤트 발행
                    sendLogEvent(response, currentAccountDTO, request);

                    // [수정] 유효성 검사 실패는 400 Bad Request여야 함 (기존 401 UNAUTHORIZED 수정)
                    return restApiController.createFailRestResponse(response, HttpStatus.BAD_REQUEST);
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

    private CurrentAccountDTO getTokenResponse(HttpServletRequest httpServletRequest) {
        TokenResponse tokenResponse;
        String authorization = httpServletRequest.getHeader("Authorization");
        String bearer;
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            bearer = authorization.split(" ")[1];
            tokenResponse = jwtTokenProvider.getTokenResponse(bearer);
            return new CurrentAccountDTO(tokenResponse);
        }
        return null;
    }
}