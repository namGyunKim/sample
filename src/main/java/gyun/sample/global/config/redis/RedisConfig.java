package gyun.sample.global.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

// Redis 설정
@Configuration
public class RedisConfig {
    //    Redis host 설정 (application.yml에서 설정 필요)
    @Value(value = "${spring.data.redis.host:localhost}")
    private String host;
    //    Redis port 설정 (application.yml에서 설정 필요)
    @Value(value = "${spring.data.redis.port:6379}")
    private int port;

    /**
     * Redis 연결 팩토리 설정
     *
     * @return RedisConnectionFactory
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }


    /**
     * 일반적인 객체 저장을 위한 RedisTemplate
     * (String 키, String 값 사용)
     *
     * @return RedisTemplate<String, String>
     */
    @Bean
    RedisTemplate<String, String> redisTemplate() {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }

    /**
     * String 기반의 키-값 쌍에 특화된 StringRedisTemplate (Blacklist에 사용)
     *
     * @return StringRedisTemplate
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate() {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setKeySerializer(new StringRedisSerializer());
        stringRedisTemplate.setValueSerializer(new StringRedisSerializer());
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory());
        return stringRedisTemplate;
    }
}