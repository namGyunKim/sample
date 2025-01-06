package gyun.sample.global.config.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig {

    @Bean(name = "emailTaskExecutor")
    public ThreadPoolTaskExecutor emailTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);     // 기본 스레드 수
        executor.setMaxPoolSize(10);     // 최대 스레드 수
        executor.setQueueCapacity(100);  // 대기열 크기
        executor.setThreadNamePrefix("EmailExecutor-");
        executor.initialize();
        return executor;
    }
}