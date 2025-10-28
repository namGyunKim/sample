package gyun.sample.global.config.web;


import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * application.yml에서 허용된 Origin 목록을 읽어 API 호출을 제어하는 필터입니다.
 */
@Component // Spring이 이 필터를 직접 관리하도록 Component로 등록합니다.
public class OriginCheckFilter implements Filter {

    // 로깅을 위한 Logger 객체 생성
    private static final Logger log = LoggerFactory.getLogger(OriginCheckFilter.class);
    private final Set<String> allowedOrigins;

    public OriginCheckFilter(@Value("${app.cors.allowed-origins}") String allowedOriginsString) {
        String[] originsArray = Arrays.stream(allowedOriginsString.split(","))
                .map(String::trim)
                .toArray(String[]::new);
        this.allowedOrigins = new HashSet<>(Arrays.asList(originsArray));
        // 애플리케이션 시작 시 허용된 Origin 목록을 로그로 출력
        log.info("OriginCheckFilter가 허용하는 Origin 목록: {}", this.allowedOrigins);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String origin = httpRequest.getHeader("Origin");
        // 모든 요청에 대해 수신된 Origin 헤더 값을 로그로 출력
        log.info("요청 수신 Origin: {}", origin);

        // OPTIONS 메소드 요청(Preflight)은 CORS 설정에서 처리하도록 그대로 통과시킵니다.
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            log.info("OPTIONS (Preflight) 요청은 통과시킵니다.");
            chain.doFilter(request, response);
            return;
        }
        // === Origin 헤더가 없는 요청(Swagger, Postman, Same-Origin 등)은 통과시킵니다. ===
        if (!StringUtils.hasText(origin)) {
            log.info("Origin 헤더가 없어 요청을 통과시킵니다.");
            chain.doFilter(request, response);
            return;
        }

        // origin 헤더가 있고, 허용된 목록(Set)에 포함되어 있는지 확인합니다.
        if (StringUtils.hasText(origin) && allowedOrigins.contains(origin)) {
            log.info("허용된 Origin 입니다. 요청을 계속 진행합니다.");
            chain.doFilter(request, response);
        } else {
            // 허용되지 않은 경우 경고 로그를 남기고 요청을 차단합니다.
            log.warn("허용되지 않은 Origin 입니다. 요청을 차단합니다. (요청 Origin: {})", origin);
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpResponse.getWriter().write("Access Denied: Invalid Origin.");
        }
    }
}