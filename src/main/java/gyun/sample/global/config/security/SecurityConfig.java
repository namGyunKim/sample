package gyun.sample.global.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * 스프링 시큐리티 설정
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    /**
     * 비밀번호 암호화를 위한 PasswordEncoder 빈
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManagerBuilder를 통해
     * UserDetailsService와 PasswordEncoder를 연결
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http
                .getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(customUserDetailsService) // DB 사용자 로드 로직
                .passwordEncoder(passwordEncoder())           // 비밀번호 암호화 매칭
                .and()
                .build();
    }

    /**
     * SecurityFilterChain - 실제 HTTP 요청에 대한 보안 설정
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1) 인증/인가 규칙
                .authorizeHttpRequests(auth -> auth
                        // 로그인 페이지("/login")는 모두 접근 가능
                        .requestMatchers("/login").permitAll()

                        // admin 경로는 ROLE_ADMIN 권한만
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // 그 외 나머지는 로그인만 되면 접근 가능
                        .anyRequest().authenticated()
                )

                // 2) 폼 로그인 설정
                .formLogin(login -> login
                        .usernameParameter("loginId")
                        .passwordParameter("password")
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .failureUrl("/login?error")   // 비번 불일치 등 실패 시
                        .defaultSuccessUrl("/", false)
                        .permitAll()
                )

                // 3) 로그아웃 설정 (예시)
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                // 4) 로그인 유지 (Remember Me) 설정 추가
                .rememberMe(rememberMe -> rememberMe
                        .key("uniqueAndSecretKey") // 서버의 키 (변경 가능)
                        .tokenValiditySeconds(1209600) // 2주 (초 단위)
                        .rememberMeParameter("remember-me") // "remember-me" 필드
                        .userDetailsService(customUserDetailsService) // 사용자 로드 서비스 설정
                );
        return http.build();
    }
}
