package gyun.sample.global.config.async;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Map;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "emailTaskExecutor")
    public AsyncTaskExecutor emailTaskExecutor() {
        // 가상 스레드 기반의 Executor 생성
        TaskExecutorAdapter adapter = new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());

        // [추가] 메인 스레드의 MDC(Trace ID 등)를 비동기 스레드로 복사하는 데코레이터
        // 이를 통해 이메일 발송 로그에도 동일한 traceId가 찍히게 됩니다.
        adapter.setTaskDecorator(runnable -> {
            // 현재 스레드(메인)의 Context 캡처
            Map<String, String> contextMap = MDC.getCopyOfContextMap();
            return () -> {
                try {
                    // 비동기 스레드에 Context 설정
                    if (contextMap != null) {
                        MDC.setContextMap(contextMap);
                    }
                    runnable.run();
                } finally {
                    // 작업 완료 후 정리
                    MDC.clear();
                }
            };
        });

        return adapter;
    }
}