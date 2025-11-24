package gyun.sample.global.config.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "emailTaskExecutor")
    public AsyncTaskExecutor emailTaskExecutor() {
        // 가상 스레드 기반의 Executor 생성
        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
    }

}