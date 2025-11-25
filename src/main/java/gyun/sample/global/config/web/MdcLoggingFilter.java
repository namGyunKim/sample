package gyun.sample.global.config.web;

import jakarta.servlet.*;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * MDC 로깅 필터
 * - 모든 HTTP 요청의 시작점에 고유한 Trace ID를 발급하여 MDC에 저장합니다.
 * - 이 ID는 Controller, Service, DB 로그 등 해당 요청의 전체 수명주기 동안 유지됩니다.
 * - 가장 먼저 실행되도록 최고 우선순위(HIGHEST_PRECEDENCE)를 부여합니다.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MdcLoggingFilter implements Filter {

    private static final String TRACE_ID = "traceId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 8자리 짧은 UUID 생성 (가독성을 위해)
        String traceId = UUID.randomUUID().toString().substring(0, 8);

        // MDC에 traceId 저장 (Logback 설정이나 application.yml logging.pattern에서 %X{traceId}로 사용 가능)
        MDC.put(TRACE_ID, traceId);

        try {
            chain.doFilter(request, response);
        } finally {
            // 요청 처리 후 반드시 MDC 정리 (스레드 풀 재사용 시 데이터 오염 방지)
            MDC.clear();
        }
    }
}