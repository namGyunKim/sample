package gyun.sample.domain.social.serviece;

import com.fasterxml.jackson.core.JsonProcessingException;
import gyun.sample.domain.social.api.KakaoApiClient;
import gyun.sample.domain.social.api.KakaoAuthClient;
import gyun.sample.domain.social.payload.request.KakaoTokenRequest;
import gyun.sample.domain.social.payload.response.KakaoInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
@ImportAutoConfiguration({FeignAutoConfiguration.class})
public class KakaoService {

    private final KakaoAuthClient kakaoAuthClient;
    private final KakaoApiClient kakaoApiClient;

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


    // TODO: 2023/09/11 json data 를 string 으로 변경 필요 
    public KakaoInfoResponse getInformation(String accessToken) throws JsonProcessingException {
        KakaoInfoResponse response = kakaoApiClient.getInformation(accessToken);
        Map<String ,Object> kakaoAccount= response.getKakaoAccount();
        System.out.println("response = " + response);
        Map<String ,Object> properties= response.getProperties();
        System.out.println("kakaoAccount = " + kakaoAccount);
        System.out.println("kakaoAccount.get(\"profile\") = " + kakaoAccount.get("profile"));
        
        System.out.println("properties = " + properties);
        System.out.println("properties.get(\"nickname\") = " + properties.get("nickname"));
        return response;
    }
}
