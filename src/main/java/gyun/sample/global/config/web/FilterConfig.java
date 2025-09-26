package gyun.sample.global.config.web;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    private final OriginCheckFilter originCheckFilter;

    // @Component로 등록된 OriginCheckFilter를 생성자에서 주입받습니다.
    public FilterConfig(OriginCheckFilter originCheckFilter) {
        this.originCheckFilter = originCheckFilter;
    }

    @Bean
    public FilterRegistrationBean<OriginCheckFilter> registerOriginCheckFilter() {
        // FilterRegistrationBean을 사용하여 필터를 등록하고 세부 설정을 합니다.
        FilterRegistrationBean<OriginCheckFilter> registrationBean = new FilterRegistrationBean<>();

        // 1. 등록할 필터를 지정합니다. (생성자에서 주입받은 필터 사용)
        registrationBean.setFilter(originCheckFilter);

        // 2. 필터를 적용할 URL 패턴을 지정합니다.
        registrationBean.addUrlPatterns("/*");
        // 3. 필터의 실행 순서를 지정합니다. 낮은 번호가 먼저 실행됩니다.
        registrationBean.setOrder(1);

        return registrationBean;
    }
}