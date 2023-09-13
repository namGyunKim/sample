package gyun.sample.domain.riot.payload.Response;

import gyun.sample.domain.riot.dto.StatusBoardDto;
import gyun.sample.domain.riot.payload.Request.PlatformStatusRequest;

import java.util.List;

public record PlatformStatusResponse(
        String id,
        List<StatusBoardDto> content
) {

    public PlatformStatusResponse(PlatformStatusRequest request,List<StatusBoardDto> content){
        this(request.getId(), content);
    }

}
