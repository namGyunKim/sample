package gyun.sample.domain.account.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository  {
    private final RedisTemplate redisTemplate;


    public void save(String refreshToken, String loginId,long refreshExpirationTime) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set(refreshToken, loginId);
        redisTemplate.expire(refreshToken, refreshExpirationTime, TimeUnit.SECONDS);
    }

    public String findByRefreshToken(String refreshToken) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        return (String) valueOperations.get(refreshToken);
    }

    public void delete(String refreshToken) {
        redisTemplate.delete(refreshToken);
    }
}
