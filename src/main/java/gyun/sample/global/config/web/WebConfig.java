package gyun.sample.global.config.web;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.global.resolver.CurrentAccountResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Web MVC 설정
 * - ArgumentResolver 등록
 * - Enum Converter 등록 (소문자 요청 -> 대문자 Enum)
 * - 정적 리소스 핸들러 설정
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final CurrentAccountResolver currentAccountResolver;

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**", "/webjars/**")
                .addResourceLocations("classpath:/templates/", "classpath:/static/", "classpath:/META-INF/resources/webjars/");

        registry.addResourceHandler("/swagger-ui.html", "/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(currentAccountResolver);
    }

    /**
     * Enum 매핑을 위한 컨버터 등록
     * URL 경로의 소문자(user)를 Enum(USER)으로 변환합니다.
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new Converter<String, AccountRole>() {
            @Override
            public AccountRole convert(String source) {
                return AccountRole.create(source); // AccountRole.create 메서드 활용
            }
        });
    }
}