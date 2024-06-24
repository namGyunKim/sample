package gyun.sample.domain.account.repository;

import gyun.sample.global.exception.JWTInterceptorException;
import gyun.sample.global.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    // RedisTemplate
    private final RedisTemplate<String, String> redisTemplate;

    // 리프레시 토큰 저장
    public void save(String refreshToken, String loginId, long refreshExpirationTime) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(loginId, refreshToken);
        redisTemplate.expire(loginId, refreshExpirationTime, TimeUnit.SECONDS);
    }

    // 리프레시 토큰으로 로그인 ID 조회
    public String findByRefreshToken(String refreshToken) {
        return findKeyByRefreshToken(refreshToken);
    }

    // 리프레시 토큰 삭제
    public String deleteWithRefreshToken(String refreshToken) {
        String loginId = findByRefreshToken(refreshToken);
        if (loginId != null) {
            redisTemplate.delete(loginId);
            return loginId;
        }
        throw new JWTInterceptorException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
    }

    //  리프레시 토큰 삭제
    public void deleteWithLoginId(String loginID) {
        boolean delete = redisTemplate.delete(loginID);
        if (!delete) {
            throw new JWTInterceptorException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }
    }

    // 모든 키를 조회하여 리프레시 토큰 값이 일치하는 키 반환
    public String findKeyByRefreshToken(String refreshToken) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        ScanOptions scanOptions = ScanOptions.scanOptions().match("*").count(1000).build();
        Cursor<byte[]> cursor = Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().scan(scanOptions);
        while (cursor.hasNext()) {
            String key = new String(cursor.next());
            String value = valueOperations.get(key);
            if (refreshToken.equals(value)) {
                return key;
            }
        }
        return null;
    }
}