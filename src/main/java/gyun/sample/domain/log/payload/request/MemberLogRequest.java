package gyun.sample.domain.log.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class MemberLogRequest {

    @Schema(description = "페이지 번호 (1부터 시작)", example = "1")
    @Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.")
    private int page = 1;

    @Schema(description = "페이지 사이즈", example = "20")
    @Min(value = 1, message = "페이지 사이즈는 1 이상이어야 합니다.")
    private int size = 20;

    @Schema(description = "검색어 (로그인 ID)", example = "user")
    private String searchWord;
}