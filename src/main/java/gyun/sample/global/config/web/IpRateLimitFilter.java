package gyun.sample.global.config.web;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component // Spring 관리 Bean으로 등록
public class IpRateLimitFilter implements Filter {

    // IP 주소별로 요청 횟수를 제한하기 위한 버킷을 저장하는 맵
    private final Map<String, Bucket> bucketsPerIp = new ConcurrentHashMap<>();

    // 새로운 버킷을 생성하는 메서드, 각 IP별로 1분에 200개의 요청을 허용
    private Bucket createNewBucket() {
        Refill refill = Refill.greedy(200, Duration.ofMinutes(1));
        Bandwidth limit = Bandwidth.classic(200, refill);

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    // 필터 체인을 통해 요청을 처리하는 메서드
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        // 클라이언트의 IP 주소를 가져옴. X-Forwarded-For 헤더 고려.
        String ip = getClientIpAddress(httpServletRequest);

        // IP 주소에 해당하는 버킷을 가져오거나 새로 생성
        Bucket bucket = getBucketForIp(ip);

        // 버킷에서 토큰을 소비하여 요청이 허용되는지 검사
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1); // tryConsumeAndReturnRemaining 사용
        if (probe.isConsumed()) {
            chain.doFilter(request, response); // 요청 처리를 계속 진행
        } else {
            respondTooManyRequests((HttpServletResponse) response, probe.getNanosToWaitForRefill() / 1_000_000_000); // 요청이 너무 많으면 에러 응답, 대기 시간 추가
        }
    }

    // IP 주소에 해당하는 버킷을 가져오는 메서드, 없으면 새로 생성
    private Bucket getBucketForIp(String ip) {
        return bucketsPerIp.computeIfAbsent(ip, k -> createNewBucket());
    }

    // 너무 많은 요청에 대한 응답을 설정하는 메서드
    private void respondTooManyRequests(HttpServletResponse response, long waitTimeInSeconds) throws IOException {
        response.setStatus(429);
        response.setHeader("Retry-After", String.valueOf(waitTimeInSeconds)); // Retry-After 헤더 추가
        response.setContentType("application/json"); // JSON 응답
        response.getWriter().write(String.format("{\"message\":\"Too many requests\", \"retryAfter\": %d}", waitTimeInSeconds));

    }

    // 클라이언트 IP 주소 가져오기 (X-Forwarded-For 헤더 고려)
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader != null) {
            return xForwardedForHeader.split(",")[0].trim(); // 첫 번째 IP 주소 반환
        }
        return request.getRemoteAddr();
    }
}