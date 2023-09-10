package gyun.sample.domain.account.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "KakaoClient", url = "https://kauth.kakao.com/oauth/authorize")
public interface KakaoClient {

    @GetMapping
    String getCode(@RequestParam("response_type") String response_type,
                    @RequestParam("client_id") String client_id,
                    @RequestParam("uri") String uri);
}