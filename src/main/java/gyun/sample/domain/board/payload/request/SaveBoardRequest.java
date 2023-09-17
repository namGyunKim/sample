package gyun.sample.domain.board.payload.request;

import gyun.sample.domain.board.enums.BoardType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SaveBoardRequest(

        @NotBlank(message = "제목을 입력해주세요.")
        @Schema(description = "제목", example = "제목")
        String title,
        @NotBlank(message = "내용을 입력해주세요.")
        @Schema(description = "내용", example = "내용")
        String content,
        @NotNull(message = "게시판 타입을 선택해주세요.")
        @Schema(description = "게시판 타입", example = "FREE")
        BoardType boardType) {
}
