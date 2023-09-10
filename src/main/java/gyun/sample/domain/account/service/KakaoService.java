package gyun.sample.domain.account.service;

import gyun.sample.domain.account.api.KakaoClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@ImportAutoConfiguration({FeignAutoConfiguration.class})
public class KakaoService {

    private final KakaoClient kakaoClient;

    @Value("${social.kakao.clientId}")
    private String clientId;
    @Value("${social.kakao.redirectUri}")
    private String redirectUri;


    public String getCode() {
        return kakaoClient.getCode("code",clientId, redirectUri);
    }


}
