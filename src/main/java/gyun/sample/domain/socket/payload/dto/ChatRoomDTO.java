package gyun.sample.domain.socket.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomDTO {
    private String id;
    private String name;
}
