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
import org.aspectj.lang.reflect.CodeSignature;
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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 컨트롤러 요청/응답 로깅 Aspect
 * - 모든 컨트롤러 메서드의 진입/종료 시점에 로그를 남깁니다.
 * - 요청 파라미터를 JSON 형태로 변환하여 변수명과 함께 출력합니다.
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ControllerLoggingAspect {

    private final ObjectMapper objectMapper;

    // 포인트컷: gyun.sample 패키지 하위의 모든 Controller 클래스의 모든 메서드
    @Pointcut("execution(* gyun.sample..*Controller.*(..))")
    private void controllerMethods() {
    }

    @Around("controllerMethods()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // RequestAttributes가 없으면(테스트 환경 등) 바로 진행
        if (RequestContextHolder.getRequestAttributes() == null) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        // Trace ID 처리: Filter에서 생성된 ID가 없으면 새로 생성 (안전 장치)
        String traceId = MDC.get("traceId");
        if (traceId == null) {
            traceId = UUID.randomUUID().toString().substring(0, 8);
            MDC.put("traceId", traceId);
        }

        String ip = UtilService.getClientIp(request);
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String loginId = getLoginIdFromSecurityContext();

        // 파라미터 정보 포맷팅 (변수명: 값)
        String params = getFormattedParams(joinPoint);

        // [REQ] 로그 출력
        log.info("\n[REQ] [{}] \nIP      : {} \nUser    : {} \nMethod  : {} \nURI     : {} \nParams  : {}",
                traceId, ip, loginId, method, uri, params);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object result = null;
        try {
            // 실제 메서드 실행
            result = joinPoint.proceed();
        } finally {
            stopWatch.stop();
            // [RES] 로그 출력 (수행 시간 포함)
            log.info("\n[RES] [{}] Time: {}ms", traceId, stopWatch.getTotalTimeMillis());
        }

        return result;
    }

    /**
     * 메서드 파라미터를 Map으로 변환하여 JSON 문자열로 반환
     * - 불필요한 객체(HttpServletRequest 등)는 제외
     * - LinkedHashMap을 사용하여 파라미터 순서 보장
     */
    private String getFormattedParams(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return "No Parameters";
        }

        try {
            // 순서를 보장하기 위해 LinkedHashMap 사용
            Map<String, Object> loggableArgs = new LinkedHashMap<>();
            String[] parameterNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();

            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                String paramName = parameterNames != null ? parameterNames[i] : "arg" + i;

                // 로깅 가능한 타입만 맵에 담기
                if (isLoggable(arg)) {
                    loggableArgs.put(paramName, arg);
                }
            }

            if (loggableArgs.isEmpty()) {
                return "None (Filtered)";
            }

            // JSON Pretty Print 적용 (줄바꿈 및 들여쓰기)
            return "\n" + objectMapper.enable(SerializationFeature.INDENT_OUTPUT).writeValueAsString(loggableArgs);

        } catch (Exception e) {
            return "Failed to parse parameters: " + e.getMessage();
        }
    }

    /**
     * 로깅에서 제외할 타입 필터링
     */
    private boolean isLoggable(Object arg) {
        return arg != null &&
                !(arg instanceof HttpServletRequest) &&
                !(arg instanceof HttpServletResponse) &&
                !(arg instanceof Model) &&
                !(arg instanceof BindingResult) &&
                !(arg instanceof MultipartFile) &&
                !(arg instanceof MultipartFile[]);
    }

    /**
     * SecurityContext에서 로그인 사용자 ID 추출
     */
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