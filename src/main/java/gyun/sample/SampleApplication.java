package gyun.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

/*
성능 관련하여 추가설명
* spring:
  jpa:
    open-in-view: false
    properties:
      hibernate:
        validator:
        default_batch_fetch_size: 100
        설정을 해놔서 굳이 반복문 돌때 모아서 in절로 가져오도록 설정해놨음 참고
* */
@SpringBootApplication
@EnableFeignClients
@EnableAsync
// @EnableJpaAuditing -> JpaAuditConfig로 이동하여 관리 (설정 분리)
public class SampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }

}