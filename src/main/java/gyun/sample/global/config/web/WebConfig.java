package gyun.sample.global.config.web;

import gyun.sample.global.config.converter.GenericEnumConverterFactory;
import gyun.sample.global.interceptor.AdminInterceptor;
import gyun.sample.global.interceptor.JWTInterceptor;
import gyun.sample.global.interceptor.LoginInterceptor;
import gyun.sample.global.interceptor.SuperAdminInterceptor;
import gyun.sample.global.resolver.CurrentAccountResolver;
import gyun.sample.global.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.format.FormatterRegistry;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    //    인터셉터
    private final JWTInterceptor jwtInterceptor;
    private final LoginInterceptor loginInterceptor;
    private final AdminInterceptor adminInterceptor;
    private final SuperAdminInterceptor superAdminInterceptor;
    private final GenericEnumConverterFactory genericEnumConverterFactory;

    // cors 설정
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("*")
                .allowedHeaders("*")
                .exposedHeaders("Authorization")
                .allowCredentials(true)
                .maxAge(3000);
    }

    //    리소스 핸들러
    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**", "/webjars/**")
                .addResourceLocations("classpath:/templates/", "classpath:/static/", "classpath:/META-INF/resources/webjars/");
    }

    //  리졸버
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        PageableHandlerMethodArgumentResolver pageResolver = new PageableHandlerMethodArgumentResolver();
        argumentResolvers.add(pageResolver);
        argumentResolvers.add(new CurrentAccountResolver(jwtTokenProvider));
    }

    //    인터셉터
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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
