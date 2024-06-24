package gyun.sample.global.event;

import lombok.Builder;
import lombok.Data;

//  로그 이벤트 객체
@Data
@Builder
public class LogEvent {
    private String message;
}
