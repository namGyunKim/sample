package gyun.sample.domain.chat.api;

import gyun.sample.domain.chat.payload.request.ChatMessageRequest;
import gyun.sample.domain.chat.service.ChatService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "ChatRestController", description = "채팅 관련 기능 api")
@RestController
@RequestMapping(value = "/api/chat")
//@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Slf4j
public class ChatRestController {

    private final ChatService chatService;


    /**
     * 구독중인사람들에게 메시지 전달 (지금은 모든사람이 하나의 채팅방을 구독하는 익명 채팅)
     * 경로는 WebSocketConfig 에서 설정한 setApplicationDestinationPrefixes 값이 prefix(chat) 로 붙음
     * /chat/send 가 됨
     */
    @MessageMapping(value = "/send")
    public void send(@RequestBody ChatMessageRequest request) {
        chatService.send(request);
    }
}
