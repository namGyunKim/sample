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
import org.slf4j.MDC;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

        // [수정] 별도의 UUID 생성 대신, Filter에서 생성한 MDC의 Trace ID를 사용합니다.
        // 만약 Filter를 타지 않는 경우(테스트 등)를 대비해 없으면 생성합니다.
        String traceId = MDC.get("traceId");
        if (traceId == null) {
            traceId = UUID.randomUUID().toString().substring(0, 8);
            MDC.put("traceId", traceId);
        }

        String ip = UtilService.getClientIp(request);
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String loginId = getLoginIdFromSecurityContext();

        // 파라미터 정보 추출
        String params = getFormattedParams(joinPoint);

        // [수정] 로그 출력 시 TraceID는 로깅 패턴(%X{traceId})에 의해 자동으로 출력되도록 설정할 예정이므로,
        // 메시지 본문에서는 중복을 피하거나 명시적으로 필요할 때만 포함합니다.
        // 여기서는 명확한 구분을 위해 포함시킵니다.
        log.info("\n[REQ] [{}] \nIP      : {} \nUser    : {} \nMethod  : {} \nURI     : {} \nParams  : {}",
                traceId, ip, loginId, method, uri, params);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object result = null;
        try {
            result = joinPoint.proceed();
        } finally {
            stopWatch.stop();
            log.info("\n[RES] [{}] Time: {}ms", traceId, stopWatch.getTotalTimeMillis());
        }

        return result;
    }

    private String getFormattedParams(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return "No Parameters";
        }

        try {
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