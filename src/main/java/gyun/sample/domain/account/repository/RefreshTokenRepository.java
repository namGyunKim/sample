package gyun.sample.domain.account.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository  {

    //  RedisTemplate
    private final RedisTemplate redisTemplate;


    //  리프레시 토큰 저장
    public void save(String refreshToken, String loginId,long refreshExpirationTime) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set(refreshToken, loginId);
        redisTemplate.expire(refreshToken, refreshExpirationTime, TimeUnit.MILLISECONDS);
    }

    //  리프레시 토큰으로 로그인 아이디 조회
    public String findByRefreshToken(String refreshToken) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        return (String) valueOperations.get(refreshToken);
    }

    //  리프레시 토큰 삭제
    public void delete(String refreshToken) {
        redisTemplate.delete(refreshToken);
    }
}
