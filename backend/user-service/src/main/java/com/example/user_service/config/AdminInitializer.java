package com.example.user_service.config;

import com.example.user_service.entity.User;
import com.example.user_service.entity.UserRole;
import com.example.user_service.entity.Gen;
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

// crearea unui user de tip admin in db local, dar si in keycloak (daca nu exista deja)
@Component
@RequiredArgsConstructor
@Slf4j
// CommandLineRunner -> se executa automat la pornirea aplicatiei
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.first-name}")
    private String adminFirstName;

    @Value("${app.admin.last-name}")
    private String adminLastName;

    @Value("${app.admin.phone}")
    private String adminPhone;

    @Value("${app.keycloak.admin-role}")
    private String adminRole;

    // verificam daca exista adminul in db local
    @Override
    public void run(String... args) {
        // verificam in db-ul local
        if (userRepository.existsByEmail(adminEmail)) {
            log.info("ADMIN user deja există în baza de date locală.");
            return;
        }

        log.info("ADMIN user nu există local.");
        createAdmin();
    }

    // daca nu exista deja:
    private void createAdmin() {
        String keycloakId = null;
        // verificam daca exista deja in keycloak
        try {
            // ne conectam la keycloak
            RealmResource realmResource = keycloak.realm(realm);
            UsersResource usersResource = realmResource.users();

            // verificam in keycloak
            List<UserRepresentation> existing = usersResource.search(adminEmail, true);
            // daca exista deja in keycloak -> il refolosim
            if (!existing.isEmpty()) {
                log.info("Userul admin există deja în Keycloak. Îl refolosim.");
                keycloakId = existing.get(0).getId();
            } else {
                // daca nu exista deja -> il cream
                UserRepresentation admin = new UserRepresentation();
                admin.setUsername(adminEmail);
                admin.setEmail(adminEmail);
                admin.setFirstName(adminFirstName);
                admin.setLastName(adminLastName);
                admin.setEnabled(true);
                admin.setEmailVerified(true);

                // setam parola
                CredentialRepresentation cred = new CredentialRepresentation();
                cred.setType(CredentialRepresentation.PASSWORD);
                cred.setValue(adminPassword);
                cred.setTemporary(false);
                admin.setCredentials(Collections.singletonList(cred));

                Response response = usersResource.create(admin);
                // daca s-a creat cu succes extragem id-ul din headerul location
                if (response.getStatus() == 201) {
                    String location = response.getHeaderString("Location");
                    keycloakId = location.substring(location.lastIndexOf('/') + 1);
                    log.info("Creat admin în Keycloak cu ID: " + keycloakId);
                } else {
                    log.error("Eroare creare admin Keycloak: " + response.getStatus());
                    return;
                }
            }

            // ii asignam rolul admin in keycloak
            assignAdminRole(realmResource, usersResource, keycloakId);

            // salvam in db
            User dbUser = new User();
            dbUser.setKeycloakId(keycloakId);
            dbUser.setEmail(adminEmail);
            dbUser.setNume(adminLastName);
            dbUser.setPrenume(adminFirstName);
            dbUser.setActive(true);

            // setam valori valide pt db, pentru a nu crapa din cauza costrangerilor not
            // null
            dbUser.setRole(UserRole.ADMIN);
            dbUser.setTelefon(adminPhone);
            dbUser.setGen(Gen.MASCULIN);

            userRepository.save(dbUser);
            log.info("ADMIN salvat cu succes în baza de date locală!");

        } catch (Exception e) {
            log.error("Eroare critică la inițializarea adminului", e);
        }
    }

    // metoda care ii asigna rolul admin in keycloak
    private void assignAdminRole(RealmResource realmResource, UsersResource usersResource, String userId) {
        try {
            RoleRepresentation role = realmResource.roles().get(adminRole).toRepresentation();

            usersResource.get(userId).roles().realmLevel().add(Collections.singletonList(role));
            log.info("Rolul 'admin' a fost asignat.");
        } catch (Exception e) {
            log.warn("ATENȚIE: Nu s-a putut asigna rolul 'admin'. Verifică dacă rolul există în Keycloak!", e);
        }
    }
}