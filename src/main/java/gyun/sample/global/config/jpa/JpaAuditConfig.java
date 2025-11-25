package gyun.sample.global.config.jpa;

import gyun.sample.global.security.PrincipalDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * JPA Auditing 설정
 * - 엔티티의 생성자/수정자 처리 로직을 정의합니다.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditConfig {

    /**
     * 현재 로그인한 사용자의 LoginId를 반환하여 @CreatedBy, @LastModifiedBy에 주입합니다.
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // 인증 정보가 없거나 익명 사용자인 경우 (회원가입 등)
            if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
                return Optional.empty(); // 또는 Optional.of("SYSTEM");
            }

            // PrincipalDetails 타입인 경우 loginId 반환
            if (authentication.getPrincipal() instanceof PrincipalDetails principalDetails) {
                return Optional.ofNullable(principalDetails.getUsername());
            }

            // 그 외의 경우 (소셜 등에서 Principal 타입이 다를 때 대비)
            return Optional.ofNullable(authentication.getName());
        };
    }
}