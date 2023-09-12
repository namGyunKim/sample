package gyun.sample.domain.social.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import gyun.sample.domain.social.payload.request.KakaoTokenRequest;
import gyun.sample.domain.social.serviece.KakaoService;
import gyun.sample.global.api.RestApiController;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


//https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/social/kakao")
public class KakaoController {

    private final KakaoService kakaoService;
    private final RestApiController restApiController;

    //     https://kauth.kakao.com/oauth/authorize?response_type=code&client_id={REST API KEY}&redirect_uri={RedirectURL}
    @Operation(summary = "카카오에 code 요청하고 redirect 받는 api")
    @GetMapping("/redirect")
    public String test(@RequestParam("code") String code) {
        return code;
//        return kakaoService.getInfo(code).getKakaoAccount().getEmail();
    }


    @Operation(summary = "code 받는 api 지만 redirect 문제가 있어서 get 방식으로 주소창에 쳐서 처리함")
    @GetMapping(value = "/get-code")
    public String getCode() {
        return kakaoService.getCode();
    }

    @Operation(summary = "코드로 카카오 로그인을 위한 토큰을 받는 api")
    @GetMapping(value = "/get-token-by-code")
    public ResponseEntity<String> getTokenByCode(@RequestParam("code") String code) {

        KakaoTokenRequest response = kakaoService.getTokenByCode(code);
        return restApiController.createRestResponse(response);
    }

    @Operation(summary = "토큰으로 정보 가입 및 로그인 처리")
    @GetMapping(value = "/save-by-token")
    public ResponseEntity<String> saveOrLoginByToken(@RequestParam("access_token") String accessToken) throws JsonProcessingException {
        return restApiController.createRestResponse(kakaoService.saveOrLoginByToken(accessToken));
    }

}
