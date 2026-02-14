package com.example.user_service.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// oferim aplicatiei un client keycloak cu drepturi de admin pentru a avea drepturile necesare in a crea si gestiona useri (spre exemplu pentru a putea crea admin-ul la pornirea aplicatiei ca in AdminInitializer)
@Configuration
public class KeycloakConfig {

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.admin.realm}")
    private String adminRealm;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.admin.username}")
    private String adminUsername;

    @Value("${keycloak.admin.password}")
    private String adminPassword;

    @Value("${keycloak.admin.client-id}")
    private String adminClientId;

    @Bean
    public Keycloak keycloakAdminClient() {
        return KeycloakBuilder.builder()
                .serverUrl(authServerUrl) // localhost:8080
                .realm(adminRealm) // master
                .username(adminUsername) // admin
                .password(adminPassword) // admin
                .clientId(adminClientId) // admin-cli
                .build();
    }
}