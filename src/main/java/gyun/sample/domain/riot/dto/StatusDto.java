package gyun.sample.domain.riot.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StatusDto {
    private int id;
    private String maintenanceStatus;
    private String incidentSeverity;
    private List<ContentDto> titles;
    private List<UpdateDto> updates;
    private String createdAt;
    private String archiveAt;
    private String updatedAt;
    private List<String> platforms;
}
