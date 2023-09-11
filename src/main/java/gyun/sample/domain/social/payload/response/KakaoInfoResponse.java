package gyun.sample.domain.social.payload.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoInfoResponse {
    private String id;
    private Object kakaoAccount;
    private Object properties;
    private String nickname;
}
