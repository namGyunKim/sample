package gyun.sample.global.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UtilService {

    private final Environment environment;

    // 클라이언트의 IP 주소를 가져오는 메소드
    public static String getClientIp(HttpServletRequest request) {
        try {
            String xForwardedForHeader = request.getHeader("X-Forwarded-For");
            if (xForwardedForHeader != null) {
                return xForwardedForHeader.split(",")[0].trim();
            } else {
                return request.getRemoteAddr();
            }
        } catch (Exception e) {
            return "IP를 가져오지 못했습니다.";
        }
    }

    // 로컬 프로필 여부를 확인하는 메서드
    public boolean isLocalProfile() {
        String[] activeProfiles = environment.getActiveProfiles();
        for (String profile : activeProfiles) {
            if (profile.equalsIgnoreCase("local")) {
                return true;
            }
        }
        return false;
    }
}
