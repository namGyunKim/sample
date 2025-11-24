package gyun.sample.domain.member.service;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.service.read.AbstractReadMemberService;
import gyun.sample.domain.member.service.read.ReadMemberService;
import gyun.sample.domain.member.service.write.AbstractWriteMemberService;
import gyun.sample.domain.member.service.write.WriteMemberService;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.enums.ErrorCode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberStrategyFactory {

    private final ApplicationContext applicationContext;

    // 전략 패턴을 위한 Map (Key: AccountRole, Value: Service)
    private final Map<AccountRole, WriteMemberService> writeServiceMap = new EnumMap<>(AccountRole.class);
    private final Map<AccountRole, ReadMemberService> readServiceMap = new EnumMap<>(AccountRole.class);

    // 기본 서비스 (필요시 사용, 여기서는 USER를 기본으로 가정 예시)
    private WriteMemberService defaultWriteService;
    private ReadMemberService defaultReadService;

    @PostConstruct
    public void init() {
        initializeWriteServices();
        initializeReadServices();
        log.info("MemberStrategyFactory Initialized. Write Services: {}, Read Services: {}", writeServiceMap.size(), readServiceMap.size());
    }

    @SuppressWarnings("rawtypes")
    private void initializeWriteServices() {
        Map<String, WriteMemberService> beans = applicationContext.getBeansOfType(WriteMemberService.class);

        for (WriteMemberService service : beans.values()) {
            if (service instanceof AbstractWriteMemberService abstractService) {
                // 서비스가 지원하는 모든 Role에 대해 Map에 등록
                List supportedRoles = abstractService.getSupportedRoles();
                for (Object role : supportedRoles) {
                    writeServiceMap.put((AccountRole) role, service);

                    // 기본 서비스 설정 (예: USER를 기본값으로 설정)
                    if (role == AccountRole.USER) {
                        defaultWriteService = service;
                    }
                }
            }
        }
    }

    private void initializeReadServices() {
        Map<String, ReadMemberService> beans = applicationContext.getBeansOfType(ReadMemberService.class);

        for (ReadMemberService service : beans.values()) {
            if (service instanceof AbstractReadMemberService abstractService) {
                // 서비스가 지원하는 모든 Role에 대해 Map에 등록
                List<AccountRole> supportedRoles = abstractService.getSupportedRoles();
                for (AccountRole role : supportedRoles) {
                    readServiceMap.put(role, service);

                    // 기본 서비스 설정
                    if (role == AccountRole.USER) {
                        defaultReadService = service;
                    }
                }
            }
        }
    }

    /**
     * AccountRole에 맞는 Write 서비스를 반환
     */
    public WriteMemberService getWriteService(AccountRole role) {
        if (role == null) {
            return getDefaultWriteService();
        }
        WriteMemberService service = writeServiceMap.get(role);
        if (service == null) {
            throw new GlobalException(ErrorCode.INPUT_VALUE_INVALID, "지원하지 않는 권한 타입입니다(Write): " + role);
        }
        return service;
    }

    public WriteMemberService getDefaultWriteService() {
        if (defaultWriteService == null) {
            throw new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR, "기본 Write 서비스가 초기화되지 않았습니다.");
        }
        return defaultWriteService;
    }

    /**
     * AccountRole에 맞는 Read 서비스를 반환
     */
    public ReadMemberService getReadService(AccountRole role) {
        if (role == null) {
            return getDefaultReadService();
        }
        ReadMemberService service = readServiceMap.get(role);
        if (service == null) {
            throw new GlobalException(ErrorCode.INPUT_VALUE_INVALID, "지원하지 않는 권한 타입입니다(Read): " + role);
        }
        return service;
    }

    public ReadMemberService getDefaultReadService() {
        if (defaultReadService == null) {
            throw new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR, "기본 Read 서비스가 초기화되지 않았습니다.");
        }
        return defaultReadService;
    }
}