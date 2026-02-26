package com.example.api_gateway.controller;

import com.example.api_gateway.dto.ConversatieAgregataDTO;
import com.example.api_gateway.service.ChatGatewayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatGatewayController {

    private final ChatGatewayService chatGatewayService;

    @GetMapping("/conversatii/agregat")
    public Mono<ResponseEntity<List<ConversatieAgregataDTO>>> getConversatiiAgregate(
            @RequestParam String userKeycloakId,   // ← keycloakId uniform, nu mai e Long userId
            @RequestParam String tipUser,
            @AuthenticationPrincipal Jwt jwt) {

        log.info("Interogare BFF Agregat pt userKeycloakId={} tipUser={}", userKeycloakId, tipUser);

        return chatGatewayService.getConversatiiAgregate(userKeycloakId, tipUser, jwt.getTokenValue())
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.ok(List.of()));
    }
}
