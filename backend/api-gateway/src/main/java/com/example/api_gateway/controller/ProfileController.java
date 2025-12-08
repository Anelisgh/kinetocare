package com.example.api_gateway.controller;

import com.example.api_gateway.service.ProfileService;
import com.example.api_gateway.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {

    private final ProfileService profileService;
    private final SecurityUtils securityUtils;

    @GetMapping
    public Mono<ResponseEntity<Map<String, Object>>> getProfile(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();
        String role = securityUtils.extractRole(jwt);

        return profileService.getProfile(keycloakId, role)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Error getting profile for user: {}", keycloakId, e);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    @PatchMapping
    public Mono<ResponseEntity<Map<String, Object>>> updateProfile(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Map<String, Object> updateData) {

        String keycloakId = jwt.getSubject();
        String role = securityUtils.extractRole(jwt);

        return profileService.updateProfile(keycloakId, role, updateData)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Error updating profile for user: {}", keycloakId, e);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }
}