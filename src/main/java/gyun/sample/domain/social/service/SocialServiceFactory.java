package gyun.sample.domain.social.service;

import gyun.sample.domain.member.enums.MemberType;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.enums.ErrorCode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

/**
 * SocialLoginService 구현체를 관리하고 MemberType에 따라 적절한 서비스를 반환하는 팩토리
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SocialServiceFactory {

    private final ApplicationContext applicationContext;
    private final Map<MemberType, SocialLoginService> serviceMap = new EnumMap<>(MemberType.class);

    @PostConstruct
    public void init() {
        Map<String, SocialLoginService> beans = applicationContext.getBeansOfType(SocialLoginService.class);
        for (SocialLoginService service : beans.values()) {
            MemberType type = service.getSupportedType();
            if (serviceMap.containsKey(type)) {
                log.warn("Duplicated SocialLoginService found for type: {}", type);
            }
            serviceMap.put(type, service);
        }
        log.info("SocialServiceFactory Initialized. Supported types: {}", serviceMap.keySet());
    }

    /**
     * MemberType에 맞는 SocialLoginService를 반환
     *
     * @param type 소셜 타입 (KAKAO, GOOGLE 등)
     * @return 해당 타입의 SocialLoginService
     * @throws GlobalException 지원하지 않는 타입인 경우
     */
    public SocialLoginService getService(MemberType type) {
        if (!type.checkSocialType()) {
            throw new GlobalException(ErrorCode.INPUT_VALUE_INVALID, "지원하지 않는 소셜 타입입니다: " + type.name());
        }
        SocialLoginService service = serviceMap.get(type);
        if (service == null) {
            throw new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR, "해당 소셜 서비스 구현체를 찾을 수 없습니다: " + type.name());
        }
        return service;
    }
}