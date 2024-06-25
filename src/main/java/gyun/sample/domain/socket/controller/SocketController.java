package gyun.sample.domain.socket.controller;

import gyun.sample.domain.socket.payload.dto.ChatMessageDTO;
import gyun.sample.domain.socket.payload.dto.ChatRoomDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * WebSocket을 통해 메시지를 처리하는 REST 컨트롤러.
 * 클라이언트의 메시지를 수신하고 브로드캐스트합니다.
 */
@Tag(name = "SocketController", description = "소켓 api")
@RestController
@RequestMapping("/api/socket")
@Slf4j
public class SocketController {

    private Map<String, ChatRoomDTO> chatRooms = new HashMap<>();
    private Map<String, Set<String>> chatRoomUsers = new HashMap<>();
    private Map<String, List<ChatMessageDTO>> chatRoomMessages = new HashMap<>();

    public SocketController() {
        chatRooms.put("public", new ChatRoomDTO("public", "Public Chat Room"));
        chatRooms.put("private", new ChatRoomDTO("private", "Private Chat Room"));
        chatRoomUsers.put("public", new HashSet<>());
        chatRoomUsers.put("private", new HashSet<>());
        chatRoomMessages.put("public", new ArrayList<>());
        chatRoomMessages.put("private", new ArrayList<>());
    }

    @GetMapping("/chatRooms")
    public Collection<ChatRoomDTO> getChatRooms() {
        return chatRooms.values();
    }

    @GetMapping("/chatRooms/{roomId}/users")
    public Set<String> getChatRoomUsers(@PathVariable String roomId) {
        return chatRoomUsers.getOrDefault(roomId, Collections.emptySet());
    }

    @GetMapping("/chatRooms/{roomId}/messages")
    public List<ChatMessageDTO> getChatRoomMessages(@PathVariable String roomId) {
        return chatRoomMessages.getOrDefault(roomId, Collections.emptyList());
    }

    @MessageMapping("/chat.sendMessage/{roomId}")
    @SendTo("/topic/{roomId}")
    public ChatMessageDTO sendMessage(@DestinationVariable String roomId, ChatMessageDTO chatMessage) {
        log.info("Received chatMessage: " + chatMessage);
        chatRoomMessages.get(roomId).add(chatMessage);
        return chatMessage;
    }

    @MessageMapping("/chat.addUser/{roomId}")
    @SendTo("/topic/{roomId}/users")
    public ChatMessageDTO addUser(@DestinationVariable String roomId, ChatMessageDTO chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        log.info("Received addUser: " + chatMessage);
        chatRoomUsers.get(roomId).add(chatMessage.getSender());
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }

    @MessageMapping("/chat.removeUser/{roomId}")
    @SendTo("/topic/{roomId}/users")
    public ChatMessageDTO removeUser(@DestinationVariable String roomId, ChatMessageDTO chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        log.info("Received removeUser: " + chatMessage);
        chatRoomUsers.get(roomId).remove(chatMessage.getSender());
        return chatMessage;
    }
}