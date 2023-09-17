package gyun.sample.domain.social.api;

import gyun.sample.domain.social.payload.request.KakaoTokenRequest;
import gyun.sample.domain.social.serviece.KakaoService;
import gyun.sample.global.api.RestApiController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


//https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api
@Tag(name = "KakaoController", description = "카카오 로그인 관련 api")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/social/kakao")
public class KakaoController {

    private final KakaoService kakaoService;
    private final RestApiController restApiController;

    //     https://kauth.kakao.com/oauth/authorize?response_type=code&client_id={REST API KEY}&redirect_uri={RedirectURL}
    @Operation(summary = "카카오에 code 요청하고 redirect 받는 api")
    @GetMapping("/redirect")
    public ResponseEntity<String> test(@RequestParam("code") String code) {
        return restApiController.createRestResponse(code);
    }

    @Operation(summary = "코드로 카카오 로그인을 위한 토큰을 받는 api")
    @GetMapping(value = "/get-token-by-code")
    public ResponseEntity<String> getTokenByCode(@RequestParam("code") String code) {

        KakaoTokenRequest response = kakaoService.getTokenByCode(code);
        return restApiController.createRestResponse(response);
    }

    @Operation(summary = "토큰으로 정보 가입 및 로그인 처리")
    @GetMapping(value = "/save-by-token")
    public ResponseEntity<String> saveOrLoginByToken(@RequestParam("access_token") String accessToken)  {
        return restApiController.createRestResponse(kakaoService.saveOrLoginByToken(accessToken));
    }

    @Operation(summary = "로그아웃 api access token 및 refresh token 만료")
    @GetMapping(value = "/logout")
    public ResponseEntity<String> logout(@RequestParam("access_token") String accessToken)  {
        return restApiController.createRestResponse(kakaoService.logout(accessToken));
    }
}
