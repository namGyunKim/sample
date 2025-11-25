package gyun.sample.global.config.social;

import gyun.sample.domain.social.google.payload.response.GoogleTokenResponse;
import gyun.sample.domain.social.google.payload.response.GoogleUserInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "googleApiClient", url = "${social.google.apiBaseUrl}", configuration = SocialApiClientConfig.class)
public interface GoogleApiClient {

    /**
     * Google OAuth 2.0 Access Token 및 ID Token을 가져오는 API 호출
     */
    @PostMapping(value = "/oauth2/v4/token", headers = "Content-Type=application/x-www-form-urlencoded")
    GoogleTokenResponse getToken(
            @RequestParam("grant_type") String grantType,
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret,
            @RequestParam("redirect_uri") String redirectUri,
            @RequestParam("code") String code
    );

    /**
     * ID Token에서 사용자 정보를 추출하거나, 별도의 API를 통해 사용자 정보를 가져옵니다.
     * Google의 /oauth2/v2/userinfo 엔드포인트는 GET 요청으로 access_token을 쿼리 파라미터로 전달하는 것이 일반적입니다.
     * (POST 요청도 지원하나, 쿼리 파라미터 대신 요청 본문 또는 Authorization 헤더를 사용하는 것이 권장됨.)
     * GET 요청으로 변경하여 가장 일반적인 방식으로 호출합니다.
     */
    @GetMapping(value = "/oauth2/v2/userinfo")
    // POST -> GET 으로 변경
    GoogleUserInfoResponse getUserInfo(
            @RequestParam("access_token") String accessToken
    );
}