package com.example.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/actuator/health").permitAll()
                        .pathMatchers("/api/auth/token", "/api/auth/logout").permitAll()
                        .pathMatchers("/api/users/auth/**").permitAll()
                        .pathMatchers("/api/chat/ws-chat/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/locatii/**").hasRole("admin")
                        .pathMatchers(HttpMethod.PATCH, "/api/locatii/**").hasRole("admin")
                        .pathMatchers(HttpMethod.DELETE, "/api/locatii/**").hasRole("admin")
                        .pathMatchers("/api/locatii/all").hasRole("admin")
                        .pathMatchers(HttpMethod.GET, "/api/locatii/**").authenticated()
                        .anyExchange().authenticated()
                )
                // orice token primit este verificat de Keycloak
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(grantedAuthoritiesExtractor()))
                )
                .build();
    }
// extrage rolurile din JWT specific WebFlux
    private Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        return jwt -> {
            Collection<GrantedAuthority> authorities = new ArrayList<>();

            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess != null && realmAccess.containsKey("roles")) {
                @SuppressWarnings("unchecked")
                Collection<String> roles = (Collection<String>) realmAccess.get("roles");
                authorities.addAll(
                        roles.stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                .collect(Collectors.toList())
                );
            }
// mono -> non-blocking, adica nu asteapta rezultatele, ci se bazeaza pe fluxuri reactive care se rezolva in viitor
            return Mono.just(new JwtAuthenticationToken(jwt, authorities));
        };
    }
}
