package gyun.sample.domain.social.api;

import gyun.sample.domain.account.payload.response.AccountLoginResponse;
import gyun.sample.domain.social.serviece.KakaoService;
import gyun.sample.global.api.RestApiController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


//https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api
@Tag(name = "KakaoController", description = "카카오 로그인 관련 api")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/social/kakao")
public class KakaoController {

    private final KakaoService kakaoService;
    private final RestApiController restApiController;

    @ModelAttribute
    public void addCustomHeader(HttpServletResponse response) {
        response.addHeader("X-Header-1", "YourHeaderValue");
    }

    //     https://kauth.kakao.com/oauth/authorize?response_type=code&client_id={REST API KEY}&redirect_uri=http://localhost:8080/api/social/kakao/redirect
    @Operation(summary = "카카오에 code 요청하고 redirect 받는 api")
    @GetMapping("/redirect")
    public ResponseEntity<String> test(@RequestParam("code") String code) {
        AccountLoginResponse response = kakaoService.login(code);
        return restApiController.createRestResponse(response);
    }
}
