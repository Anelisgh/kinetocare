package com.example.chat_service.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class StompSecurityInterceptor implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null && accessor.getCommand() != null) {
            if (StompCommand.CONNECT.equals(accessor.getCommand()) || StompCommand.SEND.equals(accessor.getCommand())) {
                String authHeader = accessor.getFirstNativeHeader("Authorization");
                
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    try {
                        Jwt jwt = jwtDecoder.decode(token);
                        JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt, jwtAuthenticationConverter.convert(jwt).getAuthorities());
                        
                        // Setăm userul pe nivel de WebSocket session (util mai ales la CONNECT)
                        accessor.setUser(authentication);
                        
                        // Populăm forțat SecurityContextHolder pentru thread-ul corent (va fi folosit de @FeignClient)
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        
                        log.debug("Securitatea STOMP (JWT) mapată cu succes pentru comanda {}", accessor.getCommand());
                    } catch (Exception e) {
                        log.warn("Preluare JWT eșuată în STOMP preSend: {}", e.getMessage());
                        SecurityContextHolder.clearContext();
                    }
                } else if (accessor.getUser() instanceof org.springframework.security.core.Authentication auth) {
                    // Dacă aveam deja User-ul setat din frame-ul CONNECT pe această sesiune:
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    log.debug("Securitatea STOMP preluată automat din accesoriu pentru comanda {}", accessor.getCommand());
                }
            }
        }
        return message;
    }

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        // Curățare obligatorie a contextului pentru a nu polua thread pool-ul
        SecurityContextHolder.clearContext();
    }
}
