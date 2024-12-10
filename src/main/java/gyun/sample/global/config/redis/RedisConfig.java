package gyun.sample.global.config.redis;

//레디스 사용할거면 주석 해제
/*import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

// Redis 설정
@Configuration
public class RedisConfig {
    //    Redis host 설정
    @Value(value = "${spring.data.redis.host}")
    private String host;
    //    Redis port 설정
    @Value(value = "${spring.data.redis.port}")
    private int port;

    //    RedisConnectionFactory 설정
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }


    //    RedisTemplate 설정
    @Bean
    RedisTemplate<String ,String> redisTemplate() {
//        redisTemplate로 get,set,delete 등의 기능을 사용할 수 있다.
        RedisTemplate<String ,String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }
}*/
