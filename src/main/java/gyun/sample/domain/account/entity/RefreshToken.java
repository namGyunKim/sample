package gyun.sample.domain.account.entity;

import jakarta.persistence.Id;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "refreshToken", timeToLive = 60)
@Getter
public class RefreshToken {

    @Id
    private String refreshToken;
    private long memberId;

    public RefreshToken(final String refreshToken, final long memberId) {
        this.refreshToken = refreshToken;
        this.memberId = memberId;
    }

}
