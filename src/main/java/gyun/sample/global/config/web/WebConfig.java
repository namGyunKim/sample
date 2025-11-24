package gyun.sample.global.config.web;

import gyun.sample.global.resolver.CurrentAccountResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Web MVC 설정
 * - 기존 권한 체크용 Interceptor 제거 (Security @PreAuthorize로 대체)
 * - ArgumentResolver, Converter 등 등록
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final CurrentAccountResolver currentAccountResolver;

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**", "/webjars/**")
                .addResourceLocations("classpath:/templates/", "classpath:/static/", "classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        // @CurrentAccount 어노테이션 처리를 위한 리졸버 등록
        argumentResolvers.add(currentAccountResolver);
    }
}