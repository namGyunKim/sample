package gyun.sample.domain.social.payload.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoTokenRequest{
    String tokenType;                   //토큰 타입 bearer로 고정
    String accessToken;                 //액세스 토큰
    String refreshToken;                //리프레시 토큰
    Long expiresIn;                     //액세스 토큰 만료 시간
    Long refreshTokenExpiresIn;         //리프레시 토큰 만료 시간
    String idToken;                      //사용자의 카카오계정 ID
}