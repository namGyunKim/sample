package gyun.sample.global.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import gyun.sample.global.security.PrincipalDetails;
import gyun.sample.global.utils.UtilService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.util.StopWatch;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ControllerLoggingAspect {

    private final ObjectMapper objectMapper;

    @Pointcut("execution(* gyun.sample..*Controller.*(..))")
    private void controllerMethods() {
    }

    @Around("controllerMethods()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        if (RequestContextHolder.getRequestAttributes() == null) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        String ip = UtilService.getClientIp(request);
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String loginId = getLoginIdFromSecurityContext();

        // 파라미터 정보 추출 (보기 좋게 포맷팅)
        String params = getFormattedParams(joinPoint);

        log.info("\n[REQ] [{}] \nIP      : {} \nUser    : {} \nMethod  : {} \nURI     : {} \nParams  : {}",
                requestId, ip, loginId, method, uri, params);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object result = null;
        try {
            result = joinPoint.proceed();
        } finally {
            stopWatch.stop();
            log.info("\n[RES] [{}] Time: {}ms", requestId, stopWatch.getTotalTimeMillis());
        }

        return result;
    }

    /**
     * 요청 파라미터를 보기 좋게 JSON 형태 등으로 변환
     */
    private String getFormattedParams(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return "No Parameters";
        }

        try {
            // 로깅에 적합하지 않은 객체 필터링 (Request, Response, Model, BindingResult, File 등)
            Map<String, Object> loggableArgs = new HashMap<>();
            String[] parameterNames = ((org.aspectj.lang.reflect.CodeSignature) joinPoint.getSignature()).getParameterNames();

            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                String paramName = parameterNames != null ? parameterNames[i] : "arg" + i;

                if (isLoggable(arg)) {
                    loggableArgs.put(paramName, arg);
                }
            }

            if (loggableArgs.isEmpty()) {
                return "None (Filtered)";
            }

            // ObjectMapper를 사용하여 JSON Pretty Print
            // (주의: 순환 참조나 Lazy Loading 이슈가 있는 엔티티는 DTO로 변환되어 들어오는 것이 안전함)
            return "\n" + objectMapper.enable(SerializationFeature.INDENT_OUTPUT).writeValueAsString(loggableArgs);

        } catch (Exception e) {
            return "Failed to parse parameters: " + e.getMessage();
        }
    }

    private boolean isLoggable(Object arg) {
        return arg != null &&
                !(arg instanceof HttpServletRequest) &&
                !(arg instanceof HttpServletResponse) &&
                !(arg instanceof Model) &&
                !(arg instanceof BindingResult) &&
                !(arg instanceof MultipartFile) &&
                !(arg instanceof MultipartFile[]);
    }

    private String getLoginIdFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken || !authentication.isAuthenticated()) {
            return "GUEST";
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof PrincipalDetails principalDetails) {
            return principalDetails.getUsername();
        } else if (principal instanceof String principalString) {
            return principalString.equals("anonymousUser") ? "GUEST" : principalString;
        }
        return "UNKNOWN";
    }
}