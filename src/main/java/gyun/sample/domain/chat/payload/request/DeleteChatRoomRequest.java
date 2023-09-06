package gyun.sample.domain.chat.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record DeleteChatRoomRequest(
        @NotBlank(message = "채팅방 아이디는 필수값입니다.")
                @Schema(description = "채팅방 아이디", example = "guestChat")
        String chatRoomId) {
}
