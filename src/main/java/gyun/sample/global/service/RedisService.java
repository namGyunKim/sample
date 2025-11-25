package gyun.sample.global.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Redis 데이터 조작 서비스
 * - 주로 JWT Access Token 블랙리스트 관리에 사용됨
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    // 블랙리스트 키에 사용할 접두사
    private static final String JWT_BLACKLIST_PREFIX = "jwt:blacklist:";
    private final StringRedisTemplate redisTemplate;

    /**
     * 데이터를 Redis에 저장하고 만료 시간을 설정합니다.
     *
     * @param key   저장할 키
     * @param value 저장할 값
     * @param ttl   만료 시간 (Duration 객체)
     */
    public void set(String key, String value, Duration ttl) {
        // 블랙리스트의 경우, 키에 접두사를 붙여 저장
        redisTemplate.opsForValue().set(JWT_BLACKLIST_PREFIX + key, value, ttl);
        log.debug("Redis set: Key={}, TTL={}", JWT_BLACKLIST_PREFIX + key, ttl);
    }

    /**
     * Redis에서 데이터를 조회합니다.
     *
     * @param key 조회할 키
     * @return 저장된 값 (없으면 null)
     */
    public String get(String key) {
        String value = redisTemplate.opsForValue().get(JWT_BLACKLIST_PREFIX + key);
        log.debug("Redis get: Key={}, Value={}", JWT_BLACKLIST_PREFIX + key, value);
        return value;
    }

    /**
     * Redis에서 데이터를 삭제합니다. (블랙리스트 무효화 용도)
     *
     * @param key 삭제할 키
     */
    public void delete(String key) {
        Boolean deleted = redisTemplate.delete(JWT_BLACKLIST_PREFIX + key);
        if (Boolean.TRUE.equals(deleted)) {
            log.debug("Redis deleted: Key={}", JWT_BLACKLIST_PREFIX + key);
        } else {
            log.warn("Redis delete failed or key not found: Key={}", JWT_BLACKLIST_PREFIX + key);
        }
    }

    /**
     * 특정 Access Token이 블랙리스트에 등록되어 있는지 확인합니다.
     *
     * @param accessToken 검증할 Access Token
     * @return 블랙리스트에 존재하면 true, 아니면 false
     */
    public boolean isBlacklisted(String accessToken) {
        return get(accessToken) != null;
    }
}