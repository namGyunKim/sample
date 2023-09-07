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
    //      redis
    private final RedisTemplate redisTemplate;

    //    properties
    @Value("${spring.data.redis.chat.timeout}")
    private long timeout;

    // TODO: 2023/09/06 1:1 혹은 1:1 다 채팅만 저장하도록 수정 list 자체는 삭제가 되기때문에 guest채팅만 저장안하면됨 list 안의 값들은 개별로 만료설정이 안됨

    //    채팅 저장
    public void save(String request) {
        ListOperations listOperations = redisTemplate.opsForList();
        listOperations.leftPush("guestChat", request);
        redisTemplate.expire(request,timeout , TimeUnit.SECONDS);
    }

    //    채팅 리스트
    public List<String> findAll() {
        ListOperations listOperations = redisTemplate.opsForList();
        return listOperations.range("guestChat",0,-1);
    }

    //    채팅 삭제
    public void delete(String request) {
        redisTemplate.delete(request);
    }

}
