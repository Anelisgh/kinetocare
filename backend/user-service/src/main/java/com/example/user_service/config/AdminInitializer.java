package com.example.user_service.config;

import com.example.user_service.entity.User;
import com.example.user_service.entity.UserRole; // Asigură-te că ai acest Enum
import com.example.user_service.entity.Gen;      // Asigură-te că ai acest Enum
import com.example.user_service.repository.UserRepository;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    @Override
    public void run(String... args) {
        String adminEmail = "admin@kinetocare.com";

        // verificam in db-ul local
        if (userRepository.existsByEmail(adminEmail)) {
            log.info("ADMIN user deja există în baza de date locală.");
            return;
        }

        log.info("ADMIN user nu există local.");
        createAdmin(adminEmail);
    }

    private void createAdmin(String email) {
        String keycloakId = null;

        try {
            RealmResource realmResource = keycloak.realm(realm);
            UsersResource usersResource = realmResource.users();

            // verificam in keycloak
            List<UserRepresentation> existing = usersResource.search(email, true);

            if (!existing.isEmpty()) {
                log.info("Userul admin există deja în Keycloak. Îl refolosim.");
                keycloakId = existing.get(0).getId();
            } else {
                // cream userul in keycloak
                UserRepresentation admin = new UserRepresentation();
                admin.setUsername(email);
                admin.setEmail(email);
                admin.setFirstName("Kinetocare");
                admin.setLastName("Admin");
                admin.setEnabled(true);
                admin.setEmailVerified(true);

                // setam parola
                CredentialRepresentation cred = new CredentialRepresentation();
                cred.setType(CredentialRepresentation.PASSWORD);
                cred.setValue("admin123");
                cred.setTemporary(false);
                admin.setCredentials(Collections.singletonList(cred));

                Response response = usersResource.create(admin);

                if (response.getStatus() == 201) {
                    String location = response.getHeaderString("Location");
                    keycloakId = location.substring(location.lastIndexOf('/') + 1);
                    log.info("Creat admin în Keycloak cu ID: " + keycloakId);
                } else {
                    log.error("Eroare creare admin Keycloak: " + response.getStatus());
                    return;
                }
            }

            // adaugam rolul admin in keycloak
            assignAdminRole(realmResource, usersResource, keycloakId);

            // salvam in db
            User dbUser = new User();
            dbUser.setKeycloakId(keycloakId);
            dbUser.setEmail(email);
            dbUser.setNume("Admin");
            dbUser.setPrenume("Kinetocare");
            dbUser.setActive(true);

            // setam valori valide pt db, pentru a nu crapa din cauza costrangerilor not null
            dbUser.setRole(UserRole.ADMIN);
            dbUser.setTelefon("0000000000");
            dbUser.setGen(Gen.MASCULIN);

            userRepository.save(dbUser);
            log.info("ADMIN salvat cu succes în baza de date locală!");

        } catch (Exception e) {
            log.error("Eroare critică la inițializarea adminului", e);
        }
    }

    private void assignAdminRole(RealmResource realmResource, UsersResource usersResource, String userId) {
        try {
            String roleName = "admin";
            RoleRepresentation role = realmResource.roles().get(roleName).toRepresentation();

            usersResource.get(userId).roles().realmLevel().add(Collections.singletonList(role));
            log.info("Rolul 'admin' a fost asignat.");
        } catch (Exception e) {
            log.warn("ATENȚIE: Nu s-a putut asigna rolul 'admin'. Verifică dacă rolul există în Keycloak!", e);
        }
    }
}