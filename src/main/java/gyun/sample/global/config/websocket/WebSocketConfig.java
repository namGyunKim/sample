package gyun.sample.global.config.websocket;

// WebSocket 설정
/*@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

//    client에서 메세지를 구독할 때, 해당 endpoint를 prefix(chat)로 사용하겠다는 의미
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/chat");
    }



//    client에서 websocket을 사용할 때, websocket을 사용할 수 있는 endpoint를 등록하는 메소드
//    websocket 으로 소켓 연결
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket").setAllowedOriginPatterns("*");
        registry.addEndpoint("/websocket").setAllowedOriginPatterns("*").withSockJS();
    }
}*/
