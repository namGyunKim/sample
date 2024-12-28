package gyun.sample.global.aop;

import gyun.sample.domain.account.payload.dto.CurrentAccountDTO;
import gyun.sample.global.utils.UtilService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ControllerLoggingAspect {

    private final ConcurrentHashMap<String, Boolean> processedRequests = new ConcurrentHashMap<>();
    private final UtilService utilService;

    @Pointcut("execution(* gyun.sample.domain..*Controller.*(..)) && !@annotation(org.springframework.web.bind.annotation.InitBinder)")
    private void controllerMethods() {
    }

    @Around("controllerMethods()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {


        if (RequestContextHolder.getRequestAttributes() == null) {
            // 요청이 없는 경우 다른 방식으로 로그를 처리하거나 예외를 던질 수 있습니다.
            return joinPoint.proceed();
        }


        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        CurrentAccountDTO currentAccountDTO = utilService.getLoginDataOrGuest();
        String requestUri = request.getRequestURI();

        String uniqueRequestId = generateUniqueRequestId();
        String clientIp = UtilService.getClientIp(request);
        String httpMethod = request.getMethod();
        String koreanTime = UtilService.getKoreanTime();
        String username = getUsername();
        String sessionId = request.getSession().getId(); // 세션 ID 추가
        String loginId = getLoginId(); // 로그인 ID 추가 (로그인 사용자만 해당

        String requestKey = generateRequestKey(clientIp, sessionId, requestUri, httpMethod); // 세션 ID 포함
        if (processedRequests.putIfAbsent(requestKey, true) != null) {
            return joinPoint.proceed(); // 이미 처리된 요청이면 실행 후 리턴
        }

        long startTime = System.currentTimeMillis();
        Object result;
        try {
            logRequestDetails(uniqueRequestId, clientIp, requestUri, httpMethod, koreanTime, loginId, username, joinPoint); // 파라미터 로그 추가
            result = joinPoint.proceed();
        } finally {
            long timeTaken = System.currentTimeMillis() - startTime;
            logExecutionTime(uniqueRequestId, timeTaken);
            processedRequests.remove(requestKey); // 요청 처리 후 제거하여 메모리 누수 방지
        }

        return result;
    }

    private String generateUniqueRequestId() {
        return UUID.randomUUID().toString();
    }

    private String getUsername() {
        CurrentAccountDTO currentAccountDTO = utilService.getLoginDataOrGuest();
        return currentAccountDTO != null ? currentAccountDTO.nickName() : "비로그인 사용자";
    }

    private String getLoginId() {
        CurrentAccountDTO currentAccountDTO = utilService.getLoginDataOrGuest();
        return currentAccountDTO != null ? currentAccountDTO.loginId() : "비로그인 사용자";
    }

    private void logRequestDetails(String uniqueRequestId, String clientIp, String requestUri,
                                   String httpMethod, String koreanTime, String loginId, String username, ProceedingJoinPoint joinPoint) {
        log.info("[{}] ================================ TOP ==================================\t\t[{}]", uniqueRequestId, uniqueRequestId);
        log.info("[{}] 요청 시간:\t\t{}", uniqueRequestId, koreanTime);
        log.info("[{}] 리퀘스트 URI:\t{}", uniqueRequestId, requestUri);
        log.info("[{}] 클라이언트 IP:\t{} 계정 아이디 : {} 계정 닉네임 : {} ", uniqueRequestId, clientIp, loginId, username);
        log.info("[{}] HTTP 메서드:\t\t{}", uniqueRequestId, httpMethod);
        logRequestParameters(uniqueRequestId, joinPoint); // 파라미터 로그 추가
    }

    private void logExecutionTime(String uniqueRequestId, long timeTaken) {
        log.info("[{}] 메서드 실행 시간:\t{}ms", uniqueRequestId, timeTaken);
        log.info("[{}] ================================ BOTTOM ================================\t\t[{}]", uniqueRequestId, uniqueRequestId);
    }

    private void logRequestParameters(String uniqueRequestId, ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] parameterValues = joinPoint.getArgs();

        if (parameterNames != null && parameterValues != null) {
            log.info("[{}] 요청 파라미터:", uniqueRequestId);
            for (int i = 0; i < parameterNames.length; i++) {
                // BindingResult 와 Model 은 로깅하지 않음
                if (parameterValues[i] instanceof org.springframework.validation.BindingResult || parameterValues[i] instanceof org.springframework.ui.Model) {
                    continue;
                }
                log.info("[{}] {}: {}", uniqueRequestId, parameterNames[i], parameterValues[i]);
            }
        } else {
            log.info("[{}] 요청 파라미터가 없습니다.", uniqueRequestId);
        }
    }

    private String generateRequestKey(String clientIp, String sessionId, String requestUri, String httpMethod) {
        return clientIp + ":" + sessionId + ":" + requestUri + ":" + httpMethod;
    }
}