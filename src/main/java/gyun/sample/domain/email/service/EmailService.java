package gyun.sample.domain.email.service;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    @Value("${spring.mail.from}")
    private String from;

    private final ThreadPoolTaskExecutor emailTaskExecutor;

    //   이메일 스레드 풀 설정
    @Async("emailTaskExecutor")
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom(from);  // 보내는 사람 이메일

        mailSender.send(message);
    }

    /**
     * 스프링 컨텍스트가 내려갈 때, 스레드 풀 종료 처리
     */
    @PreDestroy
    public void shutdownExecutor() {
        // 스레드 풀 정상 종료
        emailTaskExecutor.shutdown();
    }
}