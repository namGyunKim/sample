package gyun.sample.domain.socket.controller;

import gyun.sample.domain.account.dto.CurrentAccountDTO;
import gyun.sample.domain.socket.payload.request.SendMessageRequest;
import gyun.sample.global.annotaion.CurrentAccount;
import gyun.sample.global.api.RestApiController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * WebSocket을 통해 메시지를 처리하는 REST 컨트롤러.
 * 클라이언트의 메시지를 수신하고 브로드캐스트합니다.
 */
@Tag(name = "SocketController", description = "소켓 api")
@RestController
@RequestMapping("/api/socket")
@RequiredArgsConstructor
@Slf4j
public class SocketController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final RestApiController restApiController;

    @PostMapping("/login/send-message")
    @Operation(summary = "메시지 전송,close is message -> CLOSE_SOCKET (3-7)")
//    @MessageMapping("/sendMessage/{roomId}/{memberId}")
//    sendMessage 함수에서 메시지 보내서 SendTo 주석 처리
//    @SendTo("/topic/public/{roomId}")
    public ResponseEntity<String> sendMessage(@Valid @RequestBody SendMessageRequest sendMessageRequest, BindingResult bindingResult,
                                              @CurrentAccount CurrentAccountDTO currentAccountDTO) {

//        기능만 구현해둠
        String destination = "/topic/public/" + sendMessageRequest.roomId();
        simpMessagingTemplate.convertAndSend(destination, sendMessageRequest);

        return restApiController.createRestResponse(sendMessageRequest);
    }
}