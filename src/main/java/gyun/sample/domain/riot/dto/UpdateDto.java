package gyun.sample.domain.riot.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateDto {
    private int id;
    private String author;
    private boolean publish;
    private List<String> publishLocations;
    private List<ContentDto> translations;
    private String createdAt;
    private String updatedAt;
}
