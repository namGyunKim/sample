package gyun.sample.domain.socket.controller;

import gyun.sample.domain.socket.payload.dto.ChatMessageDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

/**
 * WebSocket을 통해 메시지를 처리하는 REST 컨트롤러.
 * 클라이언트의 메시지를 수신하고 브로드캐스트합니다.
 */
@Tag(name = "SocketController", description = "소켓 api")
@RestController(value = "/api/socket")
@Slf4j
public class SocketController {

    /**
     * 클라이언트가 "/app/chat.sendMessage"로 전송한 메시지를 처리합니다.
     * 메시지를 모든 구독자에게 브로드캐스트합니다.
     *
     * @param chatMessage 클라이언트가 전송한 메시지
     * @return 브로드캐스트할 메시지
     */
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessageDTO sendMessage(ChatMessageDTO chatMessage) {
        log.info("Received chatMessage: " + chatMessage);
        // 클라이언트로부터 받은 메시지를 그대로 반환하여 브로드캐스트
        return chatMessage;
    }

    /**
     * 새로운 사용자가 채팅방에 참여할 때 클라이언트가 "/app/chat.addUser"로 전송한 메시지를 처리합니다.
     * 새로운 사용자를 모든 구독자에게 알립니다.
     *
     * @param chatMessage    새로운 사용자가 전송한 메시지
     * @param headerAccessor 메시지 헤더 액세서
     * @return 브로드캐스트할 메시지
     */
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessageDTO addUser(ChatMessageDTO chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        log.info("Received addUser: " + chatMessage);
        // 세션에 사용자 이름을 추가
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        // 새로운 사용자가 참여했음을 알리기 위해 메시지를 반환하여 브로드캐스트
        return chatMessage;
    }
}