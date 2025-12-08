package com.example.programari_service.client;

import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

@Configuration
@Slf4j
public class FeignClientConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // 1. Luăm autentificarea curentă din contextul de securitate
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // 2. Verificăm dacă avem un token JWT valid
            if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
                Jwt jwt = (Jwt) authentication.getPrincipal();

                // 3. Adăugăm header-ul Authorization în request-ul Feign
                requestTemplate.header("Authorization", "Bearer " + jwt.getTokenValue());
                log.debug("Token JWT atașat la request-ul Feign către microserviciu.");
            } else {
                log.warn("Nu s-a găsit niciun token JWT în contextul de securitate. Request-ul Feign va fi anonim.");
            }
        };
    }
}
