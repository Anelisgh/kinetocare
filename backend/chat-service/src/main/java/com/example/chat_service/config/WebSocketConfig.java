package com.example.chat_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import org.springframework.messaging.simp.config.ChannelRegistration;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompSecurityInterceptor securityInterceptor;

    public WebSocketConfig(StompSecurityInterceptor securityInterceptor) {
        this.securityInterceptor = securityInterceptor;
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(securityInterceptor);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Activează un broker in-memory pe prefixul /queue (pentru mesaje către utilizatori specifici)
        config.enableSimpleBroker("/queue");
        // Prefixul destinațiilor mapate pe metode cu @MessageMapping
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Calea de conectare la WebSocket (Gateway strip 1 prefix -> ajunge /chat/ws-chat)
        registry.addEndpoint("/chat/ws-chat")
                .setAllowedOriginPatterns("*") // Permite toate originile pentru testare; în producție, specifică originile
                .withSockJS(); // Fallback dacă browserul nu suportă direct WebSockets
    }
}
