package gyun.sample.domain.riot.payload.Response;

import gyun.sample.domain.riot.payload.Request.SummonerRequest;

public record SummonerResponse(
String id,
    String accountId,
    String puuid,
    String name,
    int profileIconId,
    long revisionDate,
    String summonerLevel
){

    public SummonerResponse(SummonerRequest request){
        this(request.id(), request.accountId(), request.puuid(), request.name(), request.profileIconId(), request.revisionDate(), request.summonerLevel());
    }

}
