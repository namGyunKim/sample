package gyun.sample.global.aop;

import gyun.sample.global.security.PrincipalDetails;
import gyun.sample.global.utils.UtilService;
import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ControllerLoggingAspect {

    // [제거] JWT Token Provider 제거
    // private final JwtTokenProvider jwtTokenProvider;


    @Pointcut("execution(* gyun.sample.domain..*Controller.*(..))")
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
        // [수정] JWT 대신 Spring Security Context에서 loginId 가져오기
        String loginId = getLoginIdFromSecurityContext();

        log.info("[REQ] [{}] IP:{} | User:{} | {} {}", requestId, ip, loginId, method, uri);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object result = null;
        try {
            result = joinPoint.proceed();
        } finally {
            stopWatch.stop();
            log.info("[RES] [{}] Time:{}ms", requestId, stopWatch.getTotalTimeMillis());
        }

        return result;
    }

    /**
     * Spring Security Context에서 현재 로그인된 사용자의 loginId를 가져옵니다.
     */
    private String getLoginIdFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken || !authentication.isAuthenticated()) {
            return "GUEST";
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof PrincipalDetails principalDetails) {
            return principalDetails.getUsername(); // loginId 반환
        } else if (principal instanceof String principalString) {
            // 익명 사용자일 경우 "anonymousUser" 등이 반환될 수 있음
            return principalString.equals("anonymousUser") ? "GUEST" : principalString;
        }

        return "UNKNOWN";
    }
}