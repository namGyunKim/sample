package gyun.sample.domain.riot.service;


import gyun.sample.domain.riot.api.RiotClient;
import gyun.sample.domain.riot.dto.ContentDto;
import gyun.sample.domain.riot.dto.StatusBoardDto;
import gyun.sample.domain.riot.dto.StatusDto;
import gyun.sample.domain.riot.dto.UpdateDto;
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


    // TODO: 2023/09/13 추가적인 작업 필요 어떤 값을 가져올지 등등 
    //    플랫폼 별 상태 가져오는 api
    public PlatformStatusResponse getPlatformStatus() {

        PlatformStatusRequest request = riotClient.getPlatformStatus(apiKey);
        List<StatusBoardDto> statusBoardDtoList = new ArrayList<>();

        for (int i = 0; i < request.getMaintenances().size(); i++) {
            StatusDto maintenance = request.getMaintenances().get(i);
            log.info("maintenance.getId() = " + maintenance.getId());
            log.info("maintenance.getMaintenanceStatus() = " + maintenance.getMaintenanceStatus());
            log.info("maintenance.getIncidentSeverity() = " + maintenance.getIncidentSeverity());
            log.info("maintenance.getTitles() = " + maintenance.getTitles());
            log.info("maintenance.getUpdates() = " + maintenance.getUpdates());
            log.info("maintenance.getCreatedAt() = " + maintenance.getCreatedAt());
            log.info("maintenance.getArchiveAt() = " + maintenance.getArchiveAt());
            log.info("maintenance.getUpdatedAt() = " + maintenance.getUpdatedAt());
            log.info("maintenance.getPlatforms() = " + maintenance.getPlatforms());
            ContentDto titleContentDto = maintenance.getTitles().get(i);
            String title = titleContentDto.getContent();

            UpdateDto updateDto = maintenance.getUpdates().get(i);
            String description = updateDto.getTranslations().get(i).getContent();

            statusBoardDtoList.add(new StatusBoardDto(title, description));
        }

        return new PlatformStatusResponse(request, statusBoardDtoList);
//        request.getMaintenances().stream().forEach(System.out::println);
//        request.getIncidents().stream().forEach(System.out::println);
    }
}
