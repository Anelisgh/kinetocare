package com.example.api_gateway.controller;

import com.example.api_gateway.service.HomepageService;
import com.example.api_gateway.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import java.util.Map;

@RestController
@RequestMapping("/api/homepage")
@RequiredArgsConstructor
@Slf4j
public class HomepageController {

    private final HomepageService homepageService;
    private final SecurityUtils securityUtils;

    @GetMapping
    public Mono<ResponseEntity<Map<String, Object>>> getHomepage(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();
        String role = securityUtils.extractRole(jwt);

        return homepageService.getHomepageData(keycloakId, role)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Eroare la generarea homepage pentru user: {}", keycloakId, e);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }
}
