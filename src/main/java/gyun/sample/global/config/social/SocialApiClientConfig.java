package gyun.sample.global.config.social;


import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.HttpMessageConverterCustomizer;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SocialApiClientConfig {

    @Bean
    public Decoder feignDecoder(ObjectProvider<HttpMessageConverterCustomizer> customizers) {
//        HttpMessageConverter<?> jacksonConverter = new MappingJackson2HttpMessageConverter(customObjectMapper());
//        HttpMessageConverters httpMessageConverters = new HttpMessageConverters(jacksonConverter);

        var httpMessageConverters = new HttpMessageConverters();
        return new ResponseEntityDecoder(new SpringDecoder(() -> httpMessageConverters, customizers));
    }

    @Bean
    public Encoder feignEncoder() {
        var httpMessageConverters = new HttpMessageConverters();
        return new SpringEncoder(() -> httpMessageConverters);
    }
}
