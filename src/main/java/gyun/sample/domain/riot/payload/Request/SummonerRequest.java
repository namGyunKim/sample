package gyun.sample.domain.riot.payload.Request;

public record SummonerRequest (
String id,
    String accountId,
    String puuid,
    String name,
    int profileIconId,
    long revisionDate,
    String summonerLevel
){

}
