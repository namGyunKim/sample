package gyun.sample.global.config.web;

import gyun.sample.global.interceptor.JWTInterceptor;
import gyun.sample.global.resolver.CurrentAccountResolver;
import gyun.sample.global.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
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

    // cors 설정
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .exposedHeaders("Authorization")
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
//        AuthenticationPrincipalArgumentResolver authenticationPrincipalArgumentResolver = new AuthenticationPrincipalArgumentResolver();
        argumentResolvers.add(pageResolver);
//        argumentResolvers.add(authenticationPrincipalArgumentResolver);
        argumentResolvers.add(new CurrentAccountResolver(jwtTokenProvider));
    }

    //    인터셉터
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //        관리자
//        registry.addInterceptor(authInterceptor)
//                .addPathPatterns("/admin/**")
//                .excludePathPatterns("/admin/login-page","/admin/login-process");
        registry.addInterceptor(jwtInterceptor)
                .excludePathPatterns("/api/account/jwt-error");
    }
}
