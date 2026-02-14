package com.example.api_gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/terapeut")
@RequiredArgsConstructor
@Slf4j
public class LocatieController {

    private final WebClient.Builder webClientBuilder;
    private static final String TERAPEUT_SERVICE_URL = "http://localhost:8084";

    // returneaza locatiile active (dropdown disponibilitate + profil pacient) -> terapeuti-service
    @GetMapping("/locatii")
    public Mono<ResponseEntity<List<Map<String, Object>>>> getLocatii(@AuthenticationPrincipal Jwt jwt) {
        return webClientBuilder.build()
                .get()
                .uri(TERAPEUT_SERVICE_URL + "/locatii")
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Error getting locatii", e);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }
}
