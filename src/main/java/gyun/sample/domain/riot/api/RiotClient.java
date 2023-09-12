package gyun.sample.domain.riot.api;

import gyun.sample.domain.riot.payload.Request.SummonerRequest;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "RiotClient", url = "https://kr.api.riotgames.com")
public interface RiotClient {


//    code 받는 api 지만 redirect 문제가 있어서 get 방식으로 주소창에 쳐서 처리함

    @Operation(summary = "닉네임으로 소환사 정보를 가져오는 api")
    @GetMapping(value = "/lol/summoner/v4/summoners/by-name/{summonerName}")
    SummonerRequest getSummonerBySummonerName(
            @PathVariable("summonerName") String summonerName,
            @RequestParam("api_key") String apiKey
    );

}