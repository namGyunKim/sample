package gyun.sample.domain.social.api;

import gyun.sample.domain.social.payload.request.KakaoInfoRequest;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "KakaoInfoClient", url = "https://kapi.kakao.com")
public interface KakaoApiClient {

//    https://developers.kakao.com/tool/rest-api/open/get/v2-user-me
    @Operation(summary = "토큰으로 정보 얻는 api")
    @GetMapping(value = "/v2/user/me")
    KakaoInfoRequest getInformation(@RequestParam("access_token") String accessToken);

}