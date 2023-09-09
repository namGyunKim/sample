package gyun.sample.domain.chat.api;

/*import gyun.sample.domain.chat.payload.request.ChatMessageRequest;
import gyun.sample.domain.chat.payload.request.DeleteChatRoomRequest;
import gyun.sample.domain.chat.service.ChatService;
import gyun.sample.global.api.RestApiController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;*/

/*@Tag(name = "ChatRestController", description = "채팅 관련 기능 api")
@RestController
@RequestMapping(value = "/api/chat")
//@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Slf4j
public class ChatRestController {

    //    utils
    private final RestApiController restApiController;
    //    service
    private final ChatService chatService;

    *//**
     * 구독중인사람들에게 메시지 전달 (지금은 모든사람이 하나의 채팅방을 구독하는 익명 채팅)
     * 경로는 WebSocketConfig 에서 설정한 setApplicationDestinationPrefixes 값이 prefix(chat) 로 붙음
     * /chat/send 가 됨
     *//*
    @Operation(summary = "채팅방에 메시지 전송")
    @MessageMapping(value = "/send")
    @PostMapping(value = "/send")
    public void send(@RequestBody ChatMessageRequest request) {
        chatService.send(request);
    }

    @Operation(summary = "채팅방 리스트")
    @GetMapping(value = "/guest-chat-list")
    public ResponseEntity<String> getChatList(){
        return restApiController.createRestResponse(chatService.getChatList());
    }

    @Operation(summary = "채팅방 삭제")
    @PostMapping(value = "/delete")
    public ResponseEntity<String> delete(@RequestBody DeleteChatRoomRequest request){
        chatService.delete(request.chatRoomId());
        return restApiController.createRestResponse("삭제완료");
    }
}*/
