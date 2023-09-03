package namGyun.sample.global.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogEvent {

    private String message;

    public static LogEvent createLogEvent(String message) {
        return LogEvent.builder()
                .message(message)
                .build();
    }

}
