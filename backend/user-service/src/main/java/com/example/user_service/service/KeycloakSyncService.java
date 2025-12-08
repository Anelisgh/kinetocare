package com.example.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakSyncService {

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    public void updateKeycloakUser(String keycloakId, String email, String firstName, String lastName) {
        try {
            UserResource userResource = keycloak.realm(realm).users().get(keycloakId);
            UserRepresentation user = userResource.toRepresentation();

            boolean hasChanges = false;

            if (email != null && !email.equals(user.getEmail())) {
                user.setEmail(email);
                user.setUsername(email);
                user.setEmailVerified(true);
                hasChanges = true;
                log.debug("Email will be updated in Keycloak for keycloakId: {}", keycloakId);
            }

            if (firstName != null && !firstName.equals(user.getFirstName())) {
                user.setFirstName(firstName);
                hasChanges = true;
            }

            if (lastName != null && !lastName.equals(user.getLastName())) {
                user.setLastName(lastName);
                hasChanges = true;
            }

            if (hasChanges) {
                userResource.update(user);
                log.info("User data synchronized with Keycloak for keycloakId: {}", keycloakId);
            } else {
                log.debug("No changes to sync with Keycloak for keycloakId: {}", keycloakId);
            }

        } catch (Exception e) {
            log.error("Failed to update Keycloak user for keycloakId: {}", keycloakId, e);
            throw new RuntimeException("Failed to sync user data with Keycloak", e);
        }
    }

    public boolean emailExistsInKeycloak(String email, String excludeUserId) {
        try {
            var users = keycloak.realm(realm).users().search(email, true);

            return users.stream()
                    .anyMatch(user -> !user.getId().equals(excludeUserId));

        } catch (Exception e) {
            log.error("Error checking email existence in Keycloak for email: {}", email, e);
            return false;
        }
    }
}