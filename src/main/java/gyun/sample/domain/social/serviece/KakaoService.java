package gyun.sample.domain.social.serviece;

import gyun.sample.domain.social.api.KakaoAuthClient;
import gyun.sample.domain.social.api.KakaoInfoClient;
import gyun.sample.domain.social.payload.request.KakaoTokenRequest;
import gyun.sample.domain.social.payload.response.KakaoInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@ImportAutoConfiguration({FeignAutoConfiguration.class})
public class KakaoService {

    private final KakaoAuthClient kakaoAuthClient;
    private final KakaoInfoClient kakaoInfoClient;

    @Value("${social.kakao.clientId}")
    private String clientId;
    @Value("${social.kakao.redirectUri}")
    private String redirectUri;
    @Value("${social.kakao.secretKey}")
    private String clientSecret;


//    code 받는 api 지만 redirect 문제가 있어서 get 방식으로 주소창에 쳐서 처리함
    public String getCode() {
        return kakaoAuthClient.getCode("code",clientId, redirectUri);
    }

//    토큰 받는 api
    public KakaoTokenRequest getToken(String code) {
        return kakaoAuthClient.getToken("authorization_code",code,redirectUri,clientId,clientSecret);
    }


    public KakaoInfoResponse getInformation(String accessToken) {
        return kakaoInfoClient.getInformation(accessToken);
    }
}
