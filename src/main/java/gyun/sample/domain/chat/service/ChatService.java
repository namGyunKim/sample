package gyun.sample.domain.chat.service;

import gyun.sample.domain.chat.payload.request.ChatMessageRequest;
import gyun.sample.domain.chat.repository.ChatRepository;
import gyun.sample.domain.chat.utils.ChatServiceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ChatService {

    //    utils
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatServiceUtil chatServiceUtil;
    //    repository
    private final ChatRepository chatRepository;

    //    채팅 전송
    @Transactional
    public void send(ChatMessageRequest request) {
        chatRepository.save(chatServiceUtil.chatMessage(request));
        simpMessagingTemplate.convertAndSend("/topic/guest", chatServiceUtil.chatMessageMap(request));
    }

    //    최신순 조회
    public List<String> getChatList() {
        return chatRepository.findAll();
    }

    //  채팅방 삭제
    public void delete(String chatRoomId) {
        chatRepository.delete(chatRoomId);
    }
}