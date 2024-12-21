package gyun.sample.global.config.web;

import gyun.sample.global.config.converter.GenericEnumConverterFactory;
import gyun.sample.global.resolver.CurrentAccountResolver;
import gyun.sample.global.utils.UtilService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


// Web 설정
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    // utils
    private final UtilService utilService;
    //    인터셉터
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
        argumentResolvers.add(new CurrentAccountResolver(utilService));
    }


    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(genericEnumConverterFactory);
    }

}
