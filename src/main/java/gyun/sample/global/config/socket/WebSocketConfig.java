package gyun.sample.global.config.socket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket 설정 클래스.
 * WebSocketMessageBrokerConfigurer를 구현하여 WebSocket과 STOMP를 구성합니다.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 메시지 브로커 설정을 구성합니다.
     * 애플리케이션에서 사용할 메시지 브로커와 목적지(prefix)를 설정합니다.
     *
     * @param config MessageBrokerRegistry 객체
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 메시지 브로커가 "/topic" 시작하는 주소를 구독하는 클라이언트에게 메시지를 브로드캐스팅합니다.
        config.enableSimpleBroker("/topic");

        // 클라이언트가 서버로 메시지를 보낼 때 사용하는 주소의 prefix를 "/app"으로 설정합니다.
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * STOMP 엔드포인트를 등록합니다.
     * 클라이언트가 웹 소켓을 연결할 수 있는 엔드포인트를 설정합니다.
     *
     * @param registry StompEndpointRegistry 객체
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 클라이언트가 "/websocket" 엔드포인트를 통해 웹 소켓에 연결할 수 있도록 설정합니다.
        // SockJS를 사용하여 웹 소켓을 지원하지 않는 브라우저에서도 웹 소켓 연결을 사용할 수 있도록 합니다.
        registry.addEndpoint("/websocket").withSockJS();
    }
}