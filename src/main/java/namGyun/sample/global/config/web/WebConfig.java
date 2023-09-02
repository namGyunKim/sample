package namGyun.sample.global.config.web;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

//    private final JwtUtil jwtUtil;
//    private final AuthInterceptor authInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .exposedHeaders("Authorization")
                .maxAge(3000);
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**", "/webjars/**")
                .addResourceLocations("classpath:/templates/", "classpath:/static/", "classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        PageableHandlerMethodArgumentResolver pageResolver = new PageableHandlerMethodArgumentResolver();
//        AuthenticationPrincipalArgumentResolver authenticationPrincipalArgumentResolver = new AuthenticationPrincipalArgumentResolver();
        argumentResolvers.add(pageResolver);
//        argumentResolvers.add(authenticationPrincipalArgumentResolver);
//        argumentResolvers.add(new CurrentAccountResolver(jwtUtil));
    }

//    //    인터셉터
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//    //        관리자
//        registry.addInterceptor(authInterceptor)
//                .addPathPatterns("/admin/**")
//                .excludePathPatterns("/admin/login-page","/admin/login-process");
//    }

}
