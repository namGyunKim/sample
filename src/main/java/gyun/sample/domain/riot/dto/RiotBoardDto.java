package gyun.sample.domain.riot.dto;

import java.util.List;

public record RiotBoardDto(
        long id,
        String title,
        String content,
        String createAt,
        String updateAt,
        List<String> platforms
) {

}
