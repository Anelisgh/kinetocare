package com.example.api_gateway.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SecurityUtils {

    // Metodă helper pentru a obține token-ul JWT din contextul de securitate
    public Mono<String> getJwtToken() {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> {
                    if (securityContext.getAuthentication() instanceof JwtAuthenticationToken jwtAuth) {
                        return jwtAuth.getToken().getTokenValue();
                    }
                    throw new RuntimeException("No JWT token found in security context");
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Security context is empty")));
    }

    // Extrage rolul din JWT
    public String extractRole(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null && realmAccess.get("roles") instanceof List<?> roles) {
            if (roles.contains("pacient"))
                return "PACIENT";
            if (roles.contains("terapeut"))
                return "TERAPEUT";
            if (roles.contains("admin"))
                return "ADMIN";
        }
        log.warn("User with ID {} has no valid role", jwt.getSubject());
        throw new RuntimeException("Rol invalid sau neautorizat");
    }
}
