package gyun.sample.global.aop;

import gyun.sample.domain.account.payload.response.TokenResponse;
import gyun.sample.global.utils.JwtTokenProvider;
import gyun.sample.global.utils.UtilService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ControllerLoggingAspect {

    private final JwtTokenProvider jwtTokenProvider;

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
        String loginId = getLoginId(request);

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

    private String getLoginId(HttpServletRequest request) {
        try {
            String authorization = request.getHeader("Authorization");
            if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
                String bearer = authorization.split(" ")[1];
                TokenResponse token = jwtTokenProvider.getTokenResponse(bearer);
                return token.loginId() != null ? token.loginId() : "Guest";
            }
        } catch (Exception e) {
            // 토큰 파싱 에러는 무시 (Guest 처리)
        }
        return "Guest";
    }
}