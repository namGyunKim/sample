package gyun.sample.global.utils;

import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.enums.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.Environment;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

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

    //    모든 이넘 조회
    public static Map<String, List<Map<String, Object>>> getAllEnums() {
        Map<String, List<Map<String, Object>>> enums = new HashMap<>();

        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(Enum.class));
        Set<BeanDefinition> components = provider.findCandidateComponents("gyun.sample");
        for (BeanDefinition component : components) {
            // 제외할 경로 필터링
            if (Objects.requireNonNull(component.getBeanClassName()).startsWith("gyun.sample.global.exception") || component.getBeanClassName().startsWith("gyun.sample.global.error")) {
                continue;
            }

            try {
                Class<?> cls = Class.forName(component.getBeanClassName());
                if (cls.isEnum()) {
                    List<Map<String, Object>> enumValues = new ArrayList<>();
                    for (Object enumConstant : cls.getEnumConstants()) {
                        Map<String, Object> enumData = new HashMap<>();
                        enumData.put("name", ((Enum<?>) enumConstant).name());
                        try {
                            // getValue 메서드를 호출하여 값을 가져옵니다.
                            Object value = cls.getMethod("getValue").invoke(enumConstant);
                            enumData.put("value", value);
                        } catch (Exception e) {
                            log.error("Failed to get value for enum {}: {}", ((Enum<?>) enumConstant).name(), e.getMessage());
                            throw new GlobalException(ErrorCode.REFLECTION_ERROR, "Failed to get value for enum " + ((Enum<?>) enumConstant).name());
                        }
                        enumValues.add(enumData);
                    }
                    enums.put(cls.getSimpleName(), enumValues);
                }
            } catch (ClassNotFoundException e) {
                log.error("Class not found: {}", e.getMessage());
                throw new GlobalException(ErrorCode.REFLECTION_ERROR, e.getMessage());
            }
        }
        return enums;
    }

    public static Pageable getPageable(int page, int size) {
        return PageRequest.of(page - 1, size);
    }
}
