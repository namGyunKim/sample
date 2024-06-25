package gyun.sample.domain.socket.payload.dto;


import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ChatMessageDTO {
    // 메시지를 보낸 사용자
    private String sender;

    // 메시지 내용
    private String content;

    // 채팅방 ID
    private String chatRoomId;
}
