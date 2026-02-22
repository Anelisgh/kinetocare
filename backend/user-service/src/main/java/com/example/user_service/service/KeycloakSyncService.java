package com.example.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.example.user_service.exception.ResourceAlreadyExistsException;
import com.example.user_service.exception.ExternalServiceException;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakSyncService {

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    // primeste datele, le compara cu ce exista in keycloak si face update doar daca e nevoie
    public void updateKeycloakUser(String keycloakId, String email, String firstName, String lastName) {
        try {
            UserResource userResource = keycloak.realm(realm).users().get(keycloakId);
            UserRepresentation user = userResource.toRepresentation();

            boolean hasChanges = false;

            if (email != null && !email.equals(user.getEmail())) {
                // verificam daca email-ul exista deja in keycloak (exclusiv user-ul curent), fara aceasta metoda keycloak va returna o eroare (de obicei 409 conflict)
                var existingUsers = keycloak.realm(realm).users().search(email, true);
                if (!existingUsers.isEmpty()) {
                    String existingUserId = existingUsers.get(0).getId();
                    if (!existingUserId.equals(keycloakId)) {
                        log.warn("Tentativa de update cu email duplicat: {}", email);
                        throw new ResourceAlreadyExistsException("Email-ul " + email + " este deja utilizat de un alt cont!");
                    }
                }
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
            log.error("Failed to sync user data with Keycloak for user {}: {}", keycloakId, e.getMessage());
            throw new ExternalServiceException("Failed to sync user data with Keycloak", e);
        }
    }

    // verifica daca email-ul exista deja in keycloak (exclude user-ul curent)
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

    // activeaza/dezactiveaza contul in keycloak
    public void setUserEnabled(String keycloakId, boolean enabled) {
        try {
            UserResource userResource = keycloak.realm(realm).users().get(keycloakId);
            UserRepresentation user = userResource.toRepresentation();
            user.setEnabled(enabled);
            userResource.update(user);
            log.info("Keycloak user {} {} successfully", keycloakId, enabled ? "enabled" : "disabled");
        } catch (Exception e) {
            log.error("Error updating keycloak status for user {}: {}", keycloakId, e.getMessage());
            throw new ExternalServiceException("Eroare la sincronizarea cu Keycloak: " + e.getMessage(), e);
        }
    }
}