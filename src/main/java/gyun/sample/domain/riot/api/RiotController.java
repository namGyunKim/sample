package gyun.sample.domain.riot.api;

import gyun.sample.domain.riot.service.RiotService;
import gyun.sample.global.api.RestApiController;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/riot")
public class RiotController {

    private final RestApiController restApiController;
    private final RiotService riotService;

    @Operation(summary = "닉네임으로 소환사 정보를 가져오는 api")
    @GetMapping(value = "/get-summoner-by-summoner-name/{summonerName}")
    public ResponseEntity<String> getSummonerBySummonerName(@PathVariable String summonerName)  {
        riotService.getSummonerBySummonerName(summonerName);
        return restApiController.createRestResponse(true);
    }

}
