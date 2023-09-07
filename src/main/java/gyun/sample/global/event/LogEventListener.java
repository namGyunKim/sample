package gyun.sample.global.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
// 로그 이벤트 리스너
@Slf4j
@Component
public class LogEventListener {

    @Async
    @EventListener
    public void onExceptionEvent(ExceptionEvent exceptionEvent) {
        log.error(exceptionEvent.getExceptionString());
    }

    @Async
    @EventListener
    public void onLogEvent(LogEvent logEvent) {
        log.info(logEvent.getMessage());
    }

}
