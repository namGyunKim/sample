package gyun.sample.global.config.security;

import gyun.sample.global.security.PrincipalDetailsService;
import gyun.sample.global.security.filter.JwtAuthenticationFilter;
import gyun.sample.global.security.handler.JwtAccessDeniedHandler;
import gyun.sample.global.security.handler.JwtAuthenticationEntryPoint;
import gyun.sample.global.service.RedisService;
import gyun.sample.global.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security 설정
 * - 기존 Interceptor 방식에서 @PreAuthorize 방식으로 변경
 * - JWT 필터 적용 (RedisService 주입을 위해 SecurityConfig에서 인스턴스화)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true) // @PreAuthorize 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] WHITE_LIST = {
            // Swagger/API Docs 관련 경로
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/webjars/**",
            "/enums", // Enum 정보를 확인하기 위한 경로 (SwaggerConfig에서 그룹에 포함됨)

            // 공개 API 경로
            "/api/account/login",
            "/api/account/sign-up", // 만약 있다면
            "/api/account/get-token-by-refresh/**",
            "/api/health",
            "/", // IndexController
            "/api/sms/**",
            "/social/**"
    };
    private final JwtTokenProvider jwtTokenProvider;
    private final PrincipalDetailsService principalDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final RedisService redisService;

    /**
     * 비밀번호 암호화를 위한 Bean 등록
     * LoginAccountValidator 등에서 주입받아 사용됨
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                // 세션 미사용 (Stateless)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 예외 처리 핸들러
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )

                // URL별 권한 설정 (기본적으로 모두 인증 필요, WhiteList만 허용)
                // 상세 권한 체크는 Controller에서 @PreAuthorize 사용
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(WHITE_LIST).permitAll()
                        // Spring Boot 3.2+에서는 MvcRequestMatcher.antMatcher 대신
                        // antMatchers() 또는 PathRequest.toStaticResources() 사용 가능
                        // 명시적인 WHITE_LIST 설정을 통해 해결
                        .anyRequest().authenticated()
                )

                // JWT 필터 추가 (RedisService 포함하여 인스턴스화)
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, principalDetailsService, redisService),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // CORS 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // application.yml의 CORS 설정 (app.cors.allowed-origins)을 활용하도록 변경 권장
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:8080"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}