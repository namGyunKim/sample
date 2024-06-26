package gyun.sample.domain.social.api;

import feign.Headers;
import gyun.sample.domain.social.payload.request.KakaoInfoRequest;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "KakaoInfoClient", url = "https://kapi.kakao.com")
public interface KakaoApiClient {

    //    https://developers.kakao.com/tool/rest-api/open/get/v2-user-me
    @Operation(summary = "토큰으로 정보 얻는 api")
    @GetMapping(value = "/v2/user/me")
    KakaoInfoRequest getInformation(@RequestParam("access_token") String accessToken);


    //    https://developers.kakao.com/tool/rest-api/open/post/v1-user-logout
    @Operation(summary = "로그아웃 api access token 및 refresh token 만료")
    @PostMapping(value = "/v1/user/logout")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    KakaoInfoRequest logout(@RequestHeader("Authorization") String authorizationHeader);

    //    https://developers.kakao.com/tool/rest-api/open/post/v1-user-unlink
    @Operation(summary = "회원탈퇴 api access token 및 refresh token 만료")
    @PostMapping(value = "/v1/user/unlink")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    KakaoInfoRequest unlink(@RequestHeader("Authorization") String authorizationHeader);

}