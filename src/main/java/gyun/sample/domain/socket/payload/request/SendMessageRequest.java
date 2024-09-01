package gyun.sample.domain.socket.payload.request;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SendMessageRequest(

        String roomId,
        @Hidden
        long memberId,
        @NotNull(message = "메시지를 입력해주세요.")
        @NotBlank(message = "메시지를 입력해주세요.")
        String message
) {
}
