package gyun.sample.global.config.message;

import gyun.sample.global.interceptor.JWTInterceptor;
import gyun.sample.global.resolver.CurrentAccountResolver;
import gyun.sample.global.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.List;
import java.util.Locale;


// Web 설정
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class MessageConfig implements WebMvcConfigurer {

    // utils
    private final JwtTokenProvider jwtTokenProvider;
    //    인터셉터
    private final JWTInterceptor jwtInterceptor;

    //    인터셉터
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //        관리자
//        registry.addInterceptor(authInterceptor)
//                .addPathPatterns("/admin/**")
//                .excludePathPatterns("/admin/login-page","/admin/login-process");
        registry.addInterceptor(localeChangeInterceptor());
    }


    //    messages 국제화 관련 설정
    @Bean
    public LocaleResolver localeResolver() {

        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setDefaultLocale(Locale.getDefault());
        resolver.setCookieName("lang");
        return resolver;
    }

    //    lang 키 값의 쿠키 인터셉트
    //    messages 국제화 관련 설정

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    //    messages 국제화 관련 설정
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }


}
