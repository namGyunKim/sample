package gyun.sample.global.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
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

    public static Pageable getPageable(int page, int size) {
        return PageRequest.of(page - 1, size);
    }

    public static String getKoreanTime() {
        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분");
        return localDateTime.format(formatter);
    }

    public static String removeNonDigits(String input) {
        // 정규 표현식을 사용하여 숫자 이외의 모든 문자를 빈 문자열로 대체
        return input.replaceAll("[^0-9]", "");
    }

    // 날짜를 "yyyy년 MM월 dd일 HH:mm" 형식으로 포맷
    public static String formattedTime(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm");
        return localDateTime.format(formatter);
    }

    public String encodeFileName(String originalFilename) {
        try {
            return URLEncoder.encode(originalFilename, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("파일명 인코딩 실패", e);
        }
    }
}
