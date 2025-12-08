package com.example.user_service.service;

import com.example.user_service.dto.RegisterRequestDTO;
import com.example.user_service.dto.RegisterResponseDTO;
import com.example.user_service.entity.User;
import com.example.user_service.entity.UserRole;
import com.example.user_service.mapper.UserRegisterMapper;
import com.example.user_service.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakService {

    private final Keycloak keycloak;
    private final UserRepository userRepository;
    private final UserRegisterMapper userRegisterMapper;
    private final RestTemplate restTemplate;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${pacienti.service.url:http://localhost:8083}")
    private String pacientiServiceUrl;

    @Value("${terapeuti.service.url:http://localhost:8084}")
    private String terapeutiServiceUrl;

    // inregistreaza un user nou in keycloak, dar si in db
    @Transactional
    public RegisterResponseDTO registerUser(RegisterRequestDTO request) {
        // verificam rolul
        if (request.getRole() == UserRole.ADMIN) {
            log.warn("Încercare de înregistrare a unui cont ADMIN eșuată. Email: {}", request.getEmail());
            throw new RuntimeException("Înregistrarea conturilor de admin nu este permisă prin acest formular.");
        }
        // verificam in db locala
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email-ul este deja înregistrat");
        }
        String keycloakId = null;
        try {
            // cream user in keycloak
            keycloakId = createUserInKeycloak(request);

            // adauga rolul
            assignRoleInKeycloak(keycloakId, request.getRole());

            // cream user in db local
            User user = userRegisterMapper.toEntity(request, keycloakId);
            user.setActive(true);
            userRepository.save(user);
            log.info("User registered successfully: {} with role: {}", request.getEmail(), request.getRole());

            // cream profilul gol in serviciul corespunzator
            initializeRoleSpecificProfile(keycloakId, request.getRole());

            return userRegisterMapper.toRegisterResponse(user, "Cont creat cu succes! Te poți autentifica acum.");
        } catch (Exception e) {
            // daca a aparut o eroare DUPA ce user-ul a fost creat in keycloak
            if (keycloakId != null) {
                log.error("Registration failed after Keycloak user creation. Rolling back Keycloak user: {}", keycloakId);
                deleteUserInKeycloak(keycloakId);
            }
            throw new RuntimeException("Eroare la înregistrare: " + e.getMessage(), e);
        }
    }

    private void initializeRoleSpecificProfile(String keycloakId, UserRole role) {
        try {
            if (role == UserRole.PACIENT) {
                // endpoint-ul de initializare pentru pacient
                String url = pacientiServiceUrl + "/pacient/initialize/" + keycloakId;
                ResponseEntity<Void> response = restTemplate.postForEntity(url, null, Void.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    log.info("Patient profile initialized for keycloakId: {}", keycloakId);
                } else {
                    log.error("Failed to initialize patient profile, status: {}", response.getStatusCode());
                    throw new RuntimeException("Eroare la crearea profilului de pacient");
                }
            } else if (role == UserRole.TERAPEUT) {
                // endpoint-ul de creare pentru terapeut
                String url = terapeutiServiceUrl + "/terapeut/by-keycloak/" + keycloakId;
                ResponseEntity<Object> response = restTemplate.postForEntity(url, null, Object.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    log.info("Terapeut profile initialized for keycloakId: {}", keycloakId);
                } else {
                    log.error("Failed to initialize terapeut profile, status: {}", response.getStatusCode());
                    throw new RuntimeException("Eroare la crearea profilului de terapeut");
                }
            }
        } catch (Exception e) {
            log.error("Failed to initialize role-specific profile for keycloakId: {}", keycloakId, e);
            throw new RuntimeException("Eroare la inițializarea profilului specific: " + e.getMessage(), e);
        }
    }

    private void deleteUserInKeycloak(String keycloakId) {
        try {
            RealmResource realmResource = keycloak.realm(realm);
            realmResource.users().get(keycloakId).remove();
            log.info("Successfully deleted Keycloak user with id {}", keycloakId);
        } catch (Exception e) {
            log.error("Failed to delete Keycloak user with id {}. Manual cleanup might be required.", keycloakId, e);
        }
    }

    private String createUserInKeycloak(RegisterRequestDTO request) {
        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersResource = realmResource.users();

        List<UserRepresentation> existingUsers = usersResource.search(request.getEmail());
        if (!existingUsers.isEmpty()) {
            throw new RuntimeException("Utilizatorul există deja în Keycloak");
        }

        // construim obiectul
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(request.getEmail());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getNume());
        user.setLastName(request.getPrenume());
        user.setEmailVerified(true); // fara email verification

        // seteaza parola
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.getPassword());
        credential.setTemporary(false); // parola nu e temporara
        user.setCredentials(Collections.singletonList(credential));

        Response response = usersResource.create(user);

        if (response.getStatus() != 201) {
            String error = response.readEntity(String.class);
            log.error("Failed to create user in Keycloak: {}", error);
            throw new RuntimeException("Eroare la crearea contului în Keycloak: " + error);
        }

        // extragem ID-ul user-ului din location header
        String locationHeader = response.getHeaderString("Location");
        String keycloakId = locationHeader.substring(locationHeader.lastIndexOf('/') + 1);

        response.close();
        log.info("User created in Keycloak with ID: {}", keycloakId);
        return keycloakId;
    }

    private void assignRoleInKeycloak(String keycloakId, UserRole role) {
        RealmResource realmResource = keycloak.realm(realm);

        // gasește rolul din Keycloak
        String roleName = role.name().toLowerCase();
        var keycloakRole = realmResource.roles().get(roleName).toRepresentation();

        // assign rolul user-ului
        realmResource.users()
                .get(keycloakId)
                .roles()
                .realmLevel()
                .add(Collections.singletonList(keycloakRole));

        log.info("Assigned role {} to user {}", roleName, keycloakId);
    }
}
