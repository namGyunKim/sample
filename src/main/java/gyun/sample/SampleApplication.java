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
//todo: MemberRepository의 getMemberList 에서 MemberSpecification.getMemberListSpec 를 사용중인데 이걸 서비스로 이동하고 회원 목록 볼때 정렬기준 추가 디폴트는 생성일 역순
public class SampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }

}