package gyun.sample.domain.social.api;

import gyun.sample.domain.social.payload.request.KakaoTokenRequest;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "KakaoAuthClient", url = "https://kauth.kakao.com/oauth")
public interface KakaoAuthClient {



//     https://kauth.kakao.com/oauth/authorize?response_type=code&client_id={REST API KEY}&redirect_uri={RedirectURL}
    //    https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#refresh-token-info
    @Operation(summary = "카카오 로그인을 위한 토큰을 받는 api")
    @PostMapping(value = "/token")
    KakaoTokenRequest getToken(@RequestParam("grant_type") String grantType,
                               @RequestParam("code") String code,
                               @RequestParam("redirect_uri") String redirectUri,
                               @RequestParam("client_id") String clientId,
                               @RequestParam("client_secret") String clientSecret);

}