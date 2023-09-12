package gyun.sample.domain.social.payload.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.Map;

@Data
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoInfoResponse {
    private String id;
    private Map<String ,Object> kakaoAccount;
    private Map<String ,Object> properties;
    private String nickname;
}
