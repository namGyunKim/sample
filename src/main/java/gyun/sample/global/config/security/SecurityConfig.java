package gyun.sample.global.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true) // @PreAuthorize 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] WHITE_LIST = {
            // 정적 자원 (Thymeleaf/웹 환경)
            "/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico",
            // Swagger/API Docs 관련 경로
            "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/enums",

            // 공개 뷰 경로
            "/", "/account/login", "/member/user/create", "/error",

            // 공개 API 경로 (일부 RestController는 남겨둠)
            "/api/health",
            "/api/sms/**",
            "/social/**", // 소셜 로그인 (콜백 엔드포인트는 세션 발급)
            "/login" // Spring Security가 처리하는 POST 로그인 경로
    };

    // PrincipalDetailsService 대신 UserDetailsService 인터페이스 사용
    private final UserDetailsService userDetailsService;


    /**
     * 비밀번호 암호화를 위한 Bean 등록
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * DaoAuthenticationProvider 설정
     * - UserDetailsService와 PasswordEncoder를 설정하여 인증 처리 담당
     * - 폼 로그인 시 'username'과 'password' 파라미터를 사용하며, 'username'은 loginId를 의미합니다.
     * - Form Login의 기본 파라미터는 `username`과 `password`입니다.
     * - `AccountController::loginForm`에서 권한(`role`)을 받아 `UsernamePasswordAuthenticationFilter`에서
     * `username` + `role`을 하나의 String으로 합쳐 `PrincipalDetailsService::loadUserByUsername`에 전달하는 커스텀 필터가 필요할 수 있습니다.
     * 하지만, 여기서는 `PrincipalDetailsService`를 수정하여 `AccountLoginRequest`의 `loginId`와 `role`을 조합해서 찾는 로직을 반영하겠습니다.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화 (Rest API 호환성을 위해 유지. 폼 로그인을 사용하므로 활성화가 권장되지만, 현재 베이스는 비활성)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authenticationProvider(authenticationProvider())

                // 세션 사용 설정
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // 필요한 경우에만 세션 생성
                )

                // 폼 로그인 설정
                .formLogin(form -> form
                        .loginPage("/account/login")        // 사용자 정의 로그인 페이지 (GET)
                        .loginProcessingUrl("/login")       // 로그인 처리 POST 요청 경로 (templates/account/login.html의 action과 일치)
                        .usernameParameter("username")      // Spring Security 기본값 유지 (loginId + role을 조합하여 사용해야 함)
                        .passwordParameter("password")
                        .defaultSuccessUrl("/", true)       // 로그인 성공 시 리다이렉트 경로
                        .failureUrl("/account/login?error")  // 로그인 실패 시 리다이렉트 경로
                        .permitAll()
                )

                // 로그아웃 설정
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // 로그아웃 URL
                        .logoutSuccessUrl("/account/login?logout") // 로그아웃 성공 시 리다이렉트 경로
                        .invalidateHttpSession(true) // 세션 무효화
                        .deleteCookies("JSESSIONID") // 쿠키 삭제
                        .permitAll()
                )

                // URL별 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(WHITE_LIST).permitAll()
                        .anyRequest().authenticated() // 나머지 요청은 모두 인증 필요
                )
        ;

        // 참고: 폼 로그인 시 `role` 파라미터를 처리하기 위해 Custom Authentication Filter를 추가하는 것이 정석이나,
        // 현재는 `PrincipalDetailsService`가 `username`만으로도 `Member`를 찾도록 되어 있어,
        // `PrincipalDetailsService`를 수정하여 `username` + `role`을 파싱하도록 합니다. (다음 파일에서 수정)

        return http.build();
    }

    // CORS 설정 (Rest API 호환성을 위해 유지)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // application.yml의 CORS 설정 (app.cors.allowed-origins)을 활용하도록 변경 권장
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:8080"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of()); // JWT 미사용으로 Authorization 노출 제거
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}