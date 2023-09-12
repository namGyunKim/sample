package gyun.sample.domain.riot.service;


import gyun.sample.domain.riot.api.RiotClient;
import gyun.sample.domain.riot.payload.Request.SummonerRequest;
import gyun.sample.domain.riot.payload.Response.SummonerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@ImportAutoConfiguration({FeignAutoConfiguration.class})
public class RiotService {

    private final RiotClient riotClient;

    @Value("${riot.key}")
    private String apiKey;

    public SummonerResponse getSummonerBySummonerName(String summonerName) {
        SummonerRequest summoner = riotClient.getSummonerBySummonerName(summonerName, apiKey);

        return new SummonerResponse(summoner);
    }
}
