package gyun.sample.global.config.web;

import gyun.sample.global.config.converter.GenericEnumConverterFactory;
import gyun.sample.global.interceptor.AdminInterceptor;
import gyun.sample.global.interceptor.JWTInterceptor;
import gyun.sample.global.interceptor.LoginInterceptor;
import gyun.sample.global.interceptor.SuperAdminInterceptor;
import gyun.sample.global.resolver.CurrentAccountResolver;
import gyun.sample.global.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.format.FormatterRegistry;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

// Web 설정
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    // utils
    private final JwtTokenProvider jwtTokenProvider;

    // 인터셉터
    private final JWTInterceptor jwtInterceptor;
    private final LoginInterceptor loginInterceptor;
    private final AdminInterceptor adminInterceptor;
    private final SuperAdminInterceptor superAdminInterceptor;
    private final GenericEnumConverterFactory genericEnumConverterFactory;

    // 설정 파일에서 CORS 허용 목록 가져오기
    @Value("${app.cors.allowed-origins:http://localhost:3000}")
    private String[] allowedOrigins;

    // cors 설정
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins) // yml 설정 값 사용
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Authorization")
                .allowCredentials(true)
                .maxAge(3600);
    }

    // 리소스 핸들러
    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**", "/webjars/**")
                .addResourceLocations("classpath:/templates/", "classpath:/static/", "classpath:/META-INF/resources/webjars/");
    }

    // 리졸버
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        PageableHandlerMethodArgumentResolver pageResolver = new PageableHandlerMethodArgumentResolver();
        argumentResolvers.add(pageResolver);
        argumentResolvers.add(new CurrentAccountResolver(jwtTokenProvider));
    }

    // 인터셉터
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .excludePathPatterns("/api/account/jwt-error/**", "/api/account/logout", "/api/account/access-denied/**", "/social/kakao/**")
                .order(1);

        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/**/login/**")
                .excludePathPatterns("/api/account/login")
                .order(2);

        registry.addInterceptor(superAdminInterceptor)
                .addPathPatterns("/api/member/super_admin/**")
                .order(3);

        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/api/member/admin/**", "/enums")
                .order(4);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(genericEnumConverterFactory);
    }

    // Argon2 비밀번호 인코더 빈 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        // saltLength: 16 (기본값)
        // hashLength: 32 (기본값)
        // parallelism: 1 (쓰레드 경합 최소화)
        // memory: 16384 (16MB, 보안성과 성능의 균형)
        // iterations: 2 (반복 횟수)
        return new Argon2PasswordEncoder(16, 32, 1, 16384, 2);
    }
}