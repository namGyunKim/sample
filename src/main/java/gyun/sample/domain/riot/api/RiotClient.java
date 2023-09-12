package gyun.sample.domain.riot.api;

import gyun.sample.domain.riot.payload.Request.PlatformStatusRequest;
import gyun.sample.domain.riot.payload.Request.SummonerRequest;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "RiotClient", url = "https://kr.api.riotgames.com")
public interface RiotClient {



//    https://developer.riotgames.com/apis#summoner-v4/GET_getBySummonerName
    @Operation(summary = "닉네임으로 소환사 정보를 가져오는 api")
    @GetMapping(value = "/lol/summoner/v4/summoners/by-name/{summonerName}")
    SummonerRequest getSummonerBySummonerName(
            @PathVariable("summonerName") String summonerName,
            @RequestParam("api_key") String apiKey
    );



//    https://developer.riotgames.com/apis#lol-status-v4/GET_getPlatformData
    @Operation(summary = "플랫폼 별 상태 가져오는 api")
    @GetMapping(value = "/lol/status/v4/platform-data")
    PlatformStatusRequest getPlatformStatus(
            @RequestParam("api_key") String apiKey
    );

}