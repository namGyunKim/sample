package gyun.sample.domain.chat.utils;

import gyun.sample.domain.chat.enums.MessageType;
import gyun.sample.domain.chat.payload.request.ChatMessageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class ChatServiceUtil {


    public String chatMessage(ChatMessageRequest request) {
        String now = LocalDateTime.now().toString().split("T")[1];
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(now.split(":")[0]);
        sb.append(":");
        sb.append(now.split(":")[1]);
        sb.append(")");
        sb.append(request.name());
        sb.append(" : ");
        sb.append(request.message());
        return sb.toString();
    }

    public Map<String,String> chatMessageMap(ChatMessageRequest request) {
        Map<String ,String> dataMap = new HashMap<>();
        dataMap.put("name",request.name());
        dataMap.put("message",request.message());
        dataMap.put("time", nowHourAndMinute());
        dataMap.put("type", MessageType.TALK.name());
        return dataMap;
    }

    private String nowHourAndMinute(){
        String now = LocalDateTime.now().toString().split("T")[1];
        StringBuilder sb = new StringBuilder();
        sb.append(now.split(":")[0]);
        sb.append(":");
        sb.append(now.split(":")[1]);
        return sb.toString();
    }
}
