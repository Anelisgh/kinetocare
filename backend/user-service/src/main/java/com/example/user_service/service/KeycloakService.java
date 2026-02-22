package com.example.user_service.service;

import com.example.user_service.dto.RegisterRequestDTO;
import com.example.user_service.dto.RegisterResponseDTO;
import com.example.user_service.entity.User;
import com.example.user_service.entity.UserRole;
import com.example.user_service.exception.ExternalServiceException;
import com.example.user_service.exception.ForbiddenOperationException;
import com.example.user_service.exception.ResourceAlreadyExistsException;
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
        if (request.role() == UserRole.ADMIN) {
            log.warn("Încercare de înregistrare a unui cont ADMIN eșuată. Email: {}", request.email());
            throw new ForbiddenOperationException("Înregistrarea conturilor de admin nu este permisă prin acest formular.");
        }
        // verificam in db locala
        if (userRepository.existsByEmail(request.email())) {
            throw new ResourceAlreadyExistsException("Email-ul este deja înregistrat");
        }
        String keycloakId = null;
        try {
            // cream user in keycloak
            keycloakId = createUserInKeycloak(request);

            // adauga rolul
            assignRoleInKeycloak(keycloakId, request.role());

            // cream user in db local
            User user = userRegisterMapper.toEntity(request, keycloakId);
            user.setActive(true);
            userRepository.save(user);
            log.info("User registered successfully: {} with role: {}", request.email(), request.role());

            // cream profilul gol in serviciul corespunzator
            initializeRoleSpecificProfile(keycloakId, request.role());

            return userRegisterMapper.toRegisterResponse(user, "Cont creat cu succes! Te poți autentifica acum.");
        } catch (Exception e) {
            // Dacă pică DB-ul local, dar am creat în Keycloak, AR TREBUI SA STERGEM DIN KEYCLOAK AICI
            // logica de rollback (ideal cu eventual consistency dar pt acum, catch si arunca)
            log.error("Error during user registration, rolling back Keycloak if necessary", e);
            if (keycloakId != null) {
                log.warn("Attempting to delete Keycloak user {} due to registration failure.", keycloakId);
                deleteUserInKeycloak(keycloakId);
            }
            throw new ExternalServiceException("Eroare la înregistrare: " + e.getMessage(), e);
        }
    }

    // initializeaza profilul gol in serviciul corespunzator (terapeuti-service sau pacienti-service)
    private void initializeRoleSpecificProfile(String keycloakId, UserRole role) {
        try {
            if (role == UserRole.PACIENT) {
                // endpoint-ul de initializare pentru pacient
                String url = org.springframework.web.util.UriComponentsBuilder
                        .fromUriString(pacientiServiceUrl)
                        .path("/pacient/initialize/{id}")
                        .buildAndExpand(keycloakId)
                        .toUriString();
                ResponseEntity<Void> response = restTemplate.postForEntity(url, null, Void.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    log.info("Profil pacient inițializat pentru {}", keycloakId);
                } else {
                    log.error("Răspuns neașteptat la inițializarea profilului de pacient: {}",
                            response.getStatusCode());
                    throw new ExternalServiceException("Eroare la crearea profilului de pacient");
                }
            } else if (role == UserRole.TERAPEUT) {
                // endpoint-ul de initializare pentru terapeut
                String url = org.springframework.web.util.UriComponentsBuilder
                        .fromUriString(terapeutiServiceUrl)
                        .path("/terapeut/initialize/{id}")
                        .buildAndExpand(keycloakId)
                        .toUriString();
                ResponseEntity<Void> response = restTemplate.postForEntity(url, null, Void.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    log.info("Profil terapeut inițializat pentru {}", keycloakId);
                } else {
                    log.error("Răspuns neașteptat la inițializarea profilului de terapeut: {}",
                            response.getStatusCode());
                    throw new ExternalServiceException("Eroare la crearea profilului de terapeut");
                }
            }

        } catch (Exception e) {
            log.error("Exceptie la apelul serviciilor de profil", e);
            throw new ExternalServiceException("Eroare la inițializarea profilului specific: " + e.getMessage(), e);
        }
    }

    // sterge user-ul din keycloak
    private void deleteUserInKeycloak(String keycloakId) {
        try {
            RealmResource realmResource = keycloak.realm(realm);
            realmResource.users().get(keycloakId).remove();
            log.info("Successfully deleted Keycloak user with id {}", keycloakId);
        } catch (Exception e) {
            log.error("Failed to delete Keycloak user with id {}. Manual cleanup might be required.", keycloakId, e);
        }
    }

    // creaza user-ul in keycloak
    private String createUserInKeycloak(RegisterRequestDTO request) {
        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersResource = realmResource.users();

        List<UserRepresentation> existingUsers = usersResource.search(request.email(), true);
        if (!existingUsers.isEmpty()) {
            throw new ResourceAlreadyExistsException("Utilizatorul există deja în Keycloak");
        }

        // construim obiectul
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(request.email());
        user.setEmail(request.email());
        user.setFirstName(request.nume());
        user.setLastName(request.prenume());
        user.setEmailVerified(true); // fara email verification

        // seteaza parola
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.password());
        credential.setTemporary(false); // parola nu e temporara
        user.setCredentials(Collections.singletonList(credential));

        Response response = usersResource.create(user);

        if (response.getStatus() != 201) {
            log.error("Eroare la crearea utilizatorului în Keycloak. Status: {}, Info: {}",
                    response.getStatus(), response.getStatusInfo());

            String error = response.readEntity(String.class);
            log.error("Keycloak response body: {}", error);
            throw new ExternalServiceException("Eroare la crearea contului în Keycloak: " + error);
        }

        // extragem ID-ul user-ului din location header
        String locationHeader = response.getHeaderString("Location");
        String keycloakId = locationHeader.substring(locationHeader.lastIndexOf('/') + 1);

        response.close();
        log.info("User created in Keycloak with ID: {}", keycloakId);
        return keycloakId;
    }

    // atribuie rolul user-ului in keycloak
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
