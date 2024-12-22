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
    private final CustomPersistentTokenRepository customPersistentTokenRepository;
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
                .csrf().disable() // CSRF 보안 설정 비활성화 (활성화시 웹소켓 이용하려면 추가 설정 적용 필요)
                // 인증/인가 규칙
                .authorizeHttpRequests(auth -> auth
                        // 로그인 페이지("/login")는 모두 접근 가능, 하지만 인증된 사용자는 리다이렉트 처리
                        .requestMatchers("/login").permitAll()

                        // admin 경로는 ROLE_ADMIN 권한만
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // 그 외 나머지는 로그인만 되면 접근 가능
                        .anyRequest().authenticated()
                )

                // 폼 로그인 설정
                .formLogin(login -> login
                        .usernameParameter("loginId")
                        .passwordParameter("password")
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .failureUrl("/login?error")   // 비번 불일치 등 실패 시
                        .defaultSuccessUrl("/", false)
                        .permitAll()
                )

                // 로그아웃 설정
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )

                // Remember Me 설정 추가
                .rememberMe(rememberMe -> rememberMe
                        .key("sample-remember-me-key")
                        .tokenValiditySeconds(14 * 24 * 60 * 60) // 14일
                        .rememberMeParameter("remember-me")
                        .userDetailsService(customUserDetailsService)
                        .tokenRepository(customPersistentTokenRepository)
                )

                // 예외 처리 설정
                .exceptionHandling(exception -> exception
                        // 인가 실패 처리
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.sendRedirect("/access-denied");
                        })
                );
        return http.build();
    }

}
