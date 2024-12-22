package gyun.sample.domain.member.entity;

import gyun.sample.domain.account.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RememberMeToken extends BaseTimeEntity {

    @Id
    @Comment("유저 아이디")
    @Column(columnDefinition = "text")
    private String series;

    @Column(nullable = false)
    @Comment("유저 로그인 아이디")
    private String username;

    @Column(nullable = false)
    @Comment("유저 토큰")
    private String token;

    @Column(nullable = false)
    @Comment("토큰 최종 사용일")
    private LocalDateTime lastUsed;

    public RememberMeToken(PersistentRememberMeToken token) {
        this.series = token.getSeries();
        this.username = token.getUsername();
        this.token = token.getTokenValue();
        this.lastUsed = token.getDate().toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public void updateToken(String tokenValue, LocalDateTime lastUsed) {
        this.token = tokenValue;
        this.lastUsed = lastUsed;
    }
}
