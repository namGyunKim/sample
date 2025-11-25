package gyun.sample.global.config.web;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.global.resolver.CurrentAccountResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

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
        argumentResolvers.add(currentAccountResolver);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        // 소문자 String -> Enum 변환 (URL PathVariable 용)
        registry.addConverter(String.class, AccountRole.class, AccountRole::create);
    }
}