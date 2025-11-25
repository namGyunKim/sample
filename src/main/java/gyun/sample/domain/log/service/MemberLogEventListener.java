package gyun.sample.domain.log.service;

import gyun.sample.domain.log.entity.MemberLog;
import gyun.sample.domain.log.event.MemberActivityEvent;
import gyun.sample.domain.log.repository.MemberLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberLogEventListener {

    private final MemberLogRepository memberLogRepository;

    /**
     * 회원 활동 이벤트 리스너
     * 메인 트랜잭션과 분리하여 저장하거나, 비동기로 처리하여 성능 영향을 최소화합니다.
     */
    @Async
    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleMemberActivityEvent(MemberActivityEvent event) {
        try {
            MemberLog logEntity = new MemberLog(
                    event.loginId(),
                    event.memberId(),
                    event.logType(),
                    event.details(),
                    event.clientIp()
            );
            memberLogRepository.save(logEntity);

            log.info("[Activity Log] User: {}, Action: {}", event.loginId(), event.logType());
        } catch (Exception e) {
            log.error("로그 저장 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}