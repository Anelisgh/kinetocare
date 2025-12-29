package com.example.user_service.service;

import com.example.user_service.dto.UpdateUserDTO;
import com.example.user_service.dto.UserDTO;
import com.example.user_service.entity.User;
import com.example.user_service.mapper.UserMapper;
import com.example.user_service.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final KeycloakSyncService keycloakSyncService;

    public UserDTO getUserByKeycloakId(String keycloakId) {
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("Utilizator nu a fost găsit"));
        return userMapper.toDTO(user);
    }

    // Called by programari-service (pacient_id in programari = user.id)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilizator nu a fost găsit"));
        return userMapper.toDTO(user);
    }

    @Transactional
    public UserDTO updateUser(String keycloakId, UpdateUserDTO updateDTO) {
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("Utilizator nu a fost găsit"));

        userMapper.updateEntity(user, updateDTO);

        User updated = userRepository.save(user);
        log.info("Updated user with id {} ", updated.getKeycloakId());

        // Sincronizăm cu Keycloak
        try {
            keycloakSyncService.updateKeycloakUser(
                    keycloakId,
                    updateDTO.getEmail(), // email nou (sau null dacă nu s-a modificat)
                    updateDTO.getPrenume(), // prenume nou
                    updateDTO.getNume() // nume nou
            );
        } catch (Exception e) {
            log.error("CRITICAL: Failed to sync with Keycloak for user {}. Rolling back DB transaction.", keycloakId,
                    e);
            throw new RuntimeException("Eroare la sincronizarea cu Keycloak: " + e.getMessage(), e);
        }

        return userMapper.toDTO(updated);
    }
}
