package gyun.sample.domain.chat.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class ChatRepository {
    private final RedisTemplate redisTemplate;

    @Value("${spring.data.redis.chat.timeout}")
    private long timeout;

    // TODO: 2023/09/06 redis에 저장할때 만료설정이 안되서 메모리에 계속 쌓이는 이슈가 있음 익명채팅은 저장하지말고 1:1 혹은 1:1 다 채팅만 저장하도록 수정
    public void save(String request) {
        ListOperations listOperations = redisTemplate.opsForList();
        listOperations.leftPush("guestChat", request);
        redisTemplate.expire(request,timeout , TimeUnit.SECONDS);
    }

    public List<String> findAll() {
        ListOperations listOperations = redisTemplate.opsForList();
        return listOperations.range("guestChat",0,-1);
    }

    public void delete(String request) {
        redisTemplate.delete(request);
    }

}
