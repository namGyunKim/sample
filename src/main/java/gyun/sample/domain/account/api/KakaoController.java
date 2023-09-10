package gyun.sample.domain.account.api;

import gyun.sample.domain.account.service.KakaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/social/kakao")
public class KakaoController {

    private final KakaoService kakaoService;

//     https://kauth.kakao.com/oauth/authorize?response_type=code&client_id={REST API KEY}&redirect_uri={RedirectURL}
    @GetMapping("/redirect")
    public String test(@RequestParam("code") String code) {
        return code;
//        return kakaoService.getInfo(code).getKakaoAccount().getEmail();
    }


    // TODO: 2023/09/10 바로 코드 반환이 아니라 redirect 경로에 코드를 줌 그래서 index.html의 버튼하고 결과가 다름 
    @GetMapping(value = "/get-code")
    public String getCode(){
        return kakaoService.getCode();
    }



}
