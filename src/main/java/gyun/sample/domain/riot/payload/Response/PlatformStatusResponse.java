package gyun.sample.domain.riot.payload.Response;

import gyun.sample.domain.riot.dto.RiotBoardDto;
import gyun.sample.domain.riot.payload.Request.PlatformStatusRequest;

import java.util.List;

public record PlatformStatusResponse(
        List<RiotBoardDto> content
) {

    public PlatformStatusResponse(PlatformStatusRequest request,List<RiotBoardDto> content){
        this(content);
    }

}
