package gyun.sample.domain.chat.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
//입장 메시지, 퇴장 메시지, 채팅 메시지
public enum MessageType {
    ENTER("입장"), QUIT("퇴장"), TALK("채팅");



    private final String value;

    MessageType(String value) {
        this.value = value;
    }
    //  요청값으로 Enum 매칭
    @JsonCreator
    public static MessageType create(String requestValue) {
        return Stream.of(values())
                .filter(v -> v.name().equals(requestValue.toUpperCase()))
                .findFirst()
                .orElse(null);
    }
}
