package gyun.sample.domain.riot.payload.Request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gyun.sample.domain.riot.dto.StatusDto;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PlatformStatusRequest {
    private String id;
    private String name;
    private List<String> locales;
    private List<StatusDto> maintenances;
    private List<StatusDto> incidents;
}
