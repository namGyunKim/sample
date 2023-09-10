package gyun.sample.domain.account.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoToken(
        String tokenType,
        String accessToken,
        String refreshToken,
        Long expiresIn,
        Long refreshTokenExpiresIn
) {

}