package gyun.sample.domain.chat.service;

import gyun.sample.domain.chat.enums.MessageType;
import gyun.sample.domain.chat.payload.request.ChatMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class ChatService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public void send(ChatMessageRequest request) {
        Map<String , Object> dataMap = new HashMap<>();
        dataMap.put("name",request.name());
        dataMap.put("message",request.message());
        dataMap.put("time", nowHourAndMinute());
        dataMap.put("type", MessageType.TALK.name());
        simpMessagingTemplate.convertAndSend("/topic/guest",dataMap);
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