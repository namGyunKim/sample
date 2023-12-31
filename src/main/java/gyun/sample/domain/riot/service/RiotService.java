package gyun.sample.domain.riot.service;


import gyun.sample.domain.riot.api.RiotClient;
import gyun.sample.domain.riot.dto.RiotBoardDto;
import gyun.sample.domain.riot.dto.StatusDto;
import gyun.sample.domain.riot.payload.Request.PlatformStatusRequest;
import gyun.sample.domain.riot.payload.Request.SummonerRequest;
import gyun.sample.domain.riot.payload.Response.PlatformStatusResponse;
import gyun.sample.domain.riot.payload.Response.SummonerResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@ImportAutoConfiguration({FeignAutoConfiguration.class})
public class RiotService {

    private final RiotClient riotClient;

    @Value("${riot.key}")
    private String apiKey;

    //    닉네임으로 소환사 정보를 가져오는 api
    public SummonerResponse getSummonerBySummonerName(String summonerName) {
        SummonerRequest summoner = riotClient.getSummonerBySummonerName(summonerName, apiKey);

        return new SummonerResponse(summoner);
    }


    public PlatformStatusResponse getPlatformStatus() {

        PlatformStatusRequest request = riotClient.getPlatformStatus(apiKey);
        List<RiotBoardDto> statusBoardDtoList = new ArrayList<>();


        for (int i = 0; i < request.getMaintenances().size(); i++) {
            StatusDto maintenance = request.getMaintenances().get(i);
            StringBuilder stringBuilder = new StringBuilder();
            long id = maintenance.getId();
            String title = maintenance.getTitles().stream().filter(contentDto -> contentDto.getLocale().equals("ko_KR")).findFirst().get().getContent();
//            update 내용이 여러개일 경우
            for (int j = 0; j < maintenance.getUpdates().size(); j++) {
                String description = maintenance.getUpdates().get(j).getTranslations().stream().filter(updateDto -> updateDto.getLocale().equals("ko_KR")).findFirst().get().getContent();
                stringBuilder.append("\n");
                stringBuilder.append(description);
            }
            String createAt = maintenance.getCreatedAt();
            String updateAt = maintenance.getUpdatedAt();
            List<String> platforms = maintenance.getPlatforms();


            statusBoardDtoList.add(new RiotBoardDto(id,title, stringBuilder.toString(), createAt.split("\\.")[0], updateAt.split("\\.")[0], platforms));

        }

        return new PlatformStatusResponse(request, statusBoardDtoList);
//        request.getMaintenances().stream().forEach(System.out::println);
//        request.getIncidents().stream().forEach(System.out::println);
    }
}
