package gyun.sample.domain.board.payload.request;

import gyun.sample.domain.board.enums.BoardType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateBoardRequest(
        @NotNull(message = "제목을 입력해주세요.")
        @NotBlank(message = "제목을 입력해주세요.")
        @Schema(description = "제목", example = "제목")
        String title,
        @NotNull(message = "내용을 입력해주세요.")
        @NotBlank(message = "내용을 입력해주세요.")
        @Schema(description = "내용", example = "내용")
        String content,
        @Schema(description = "공지 여부", example = "false")
        boolean notice,
        @NotNull(message = "게시판 타입을 입력해주세요.")
        BoardType boardType,
        @Schema(description = "클랜 아이디", example = "1")
        long clanId
) {
}
