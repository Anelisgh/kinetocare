package com.example.user_service.service;

import com.example.user_service.client.PacientiClient;
import com.example.user_service.client.ProgramariClient;
import com.example.user_service.client.TerapeutiClient;
import com.example.user_service.dto.AdminUserDTO;
import com.example.user_service.dto.UpdateUserDTO;
import com.example.user_service.dto.UserDTO;
import com.example.user_service.entity.User;
import com.example.user_service.entity.UserRole;
import com.example.user_service.exception.ExternalServiceException;
import com.example.user_service.exception.ForbiddenOperationException;
import com.example.user_service.exception.ResourceNotFoundException;
import com.example.user_service.mapper.UserMapper;
import com.example.user_service.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final KeycloakSyncService keycloakSyncService;
    private final TerapeutiClient terapeutiClient;
    private final PacientiClient pacientiClient;
    private final ProgramariClient programariClient;

    @Transactional(readOnly = true)
    public UserDTO getUserByKeycloakId(String keycloakId) {
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilizator nu a fost găsit cu ID-ul Keycloak furnizat"));
        return userMapper.toDTO(user);
    }

    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilizator nu a fost găsit"));
        return userMapper.toDTO(user);
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByKeycloakIds(List<String> keycloakIds) {
        List<User> users = userRepository.findByKeycloakIdIn(keycloakIds);
        return users.stream()
                .map(userMapper::toDTO)
                .toList(); // Java 16+ list collector
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByIds(List<Long> ids) {
        return userRepository.findAllById(ids).stream()
                .map(userMapper::toDTO)
                .toList();
    }

    @Transactional
    public UserDTO updateUser(String keycloakId, UpdateUserDTO updateDTO) {
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilizator nu a fost găsit"));

        userMapper.updateEntity(user, updateDTO);

        User updated = userRepository.save(user);
        log.info("Updated user with id {} ", updated.getKeycloakId());

        // Sincronizăm cu Keycloak
        try {
            keycloakSyncService.updateKeycloakUser(
                    keycloakId,
                    updateDTO.email(),
                    updateDTO.prenume(),
                    updateDTO.nume()
            );
        } catch (Exception e) {
            log.error("CRITICAL: Failed to sync with Keycloak for user {}. Rolling back DB transaction.", keycloakId,
                    e);
            throw new ExternalServiceException("Eroare la sincronizarea cu Keycloak: " + e.getMessage(), e);
        }

        return userMapper.toDTO(updated);
    }

    // ADMIN: dezactivare/reactivare cont
    @Transactional
    public AdminUserDTO toggleUserActive(String keycloakId) {
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("Utilizator nu a fost găsit"));

        if (user.getRole() == UserRole.ADMIN) {
            throw new ForbiddenOperationException("Nu se poate dezactiva un cont de admin");
        }

        boolean newActive = !user.getActive();
        user.setActive(newActive);
        User saved = userRepository.save(user);
        log.info("User {} (keycloakId={}) {} in DB", user.getEmail(), keycloakId, newActive ? "reactivat" : "dezactivat");

        // 1. sincronizare Keycloak (enabled/disabled)
        try {
            keycloakSyncService.setUserEnabled(user.getKeycloakId(), newActive);
        } catch (Exception e) {
            log.warn("ATTENTION: User {} modificat in DB dar Keycloak sync a esuat. " +
                     "Userul poate inca loga. Eroare: {}", keycloakId, e.getMessage());
        }

        // 2. propagare catre serviciul de profil
        propagateActiveToProfile(user.getKeycloakId(), user.getRole(), newActive);

        // 3. daca dezactivam -> anulam programarile viitoare
        if (!newActive) {
            cancelFutureAppointments(user.getKeycloakId(), user.getRole());
        }

        return userMapper.toAdminDTO(saved);
    }

    // listing useri cu filtre optionale
    @Transactional(readOnly = true)
    public List<AdminUserDTO> getUsers(UserRole role, Boolean active) {
        List<User> users;

        if (role != null && active != null) {
            users = userRepository.findByRoleAndActive(role, active);
        } else if (role != null) {
            users = userRepository.findByRole(role);
        } else if (active != null) {
            users = userRepository.findByActive(active);
        } else {
            users = userRepository.findAll();
        }

        return users.stream()
                .map(userMapper::toAdminDTO)
                .collect(Collectors.toList());
    }

    // propaga starea active catre terapeuti-service sau pacienti-service
    private void propagateActiveToProfile(String keycloakId, UserRole role, boolean active) {
        try {
            if (role == UserRole.TERAPEUT) {
                terapeutiClient.toggleActive(keycloakId, active);
            } else if (role == UserRole.PACIENT) {
                pacientiClient.toggleActive(keycloakId, active);
            }
            log.info("Propagat active={} catre {} pentru keycloakId={}", active, role, keycloakId);
        } catch (Exception e) {
            log.error("Eroare la propagarea starii active catre {} pentru {}: {}", role, keycloakId, e.getMessage());
        }
    }

    // anuleaza programarile viitoare la dezactivare
    private void cancelFutureAppointments(String keycloakId, UserRole role) {
        try {
            if (role == UserRole.TERAPEUT) {
                programariClient.cancelByTerapeut(keycloakId);
            } else if (role == UserRole.PACIENT) {
                programariClient.cancelByPacient(keycloakId);
            }
            log.info("Programări viitoare anulate pentru {} keycloakId={}", role, keycloakId);
        } catch (Exception e) {
            log.error("Eroare la anularea programărilor pentru {} {}: {}", role, keycloakId, e.getMessage());
        }
    }
}

