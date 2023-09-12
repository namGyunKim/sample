package gyun.sample.domain.chat.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ChatMessageRequest(
        @NotBlank(message = "닉네임은 필수값입니다.")
        @Schema(description = "닉네임", example = "홍길동")
        String nickName,
        @NotBlank(message = "메시지는 필수값입니다.")
        @Schema(description = "메시지", example = "안녕하세요")
        String message
) {
}
