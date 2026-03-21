package com.example.user_service.controller;

import com.example.user_service.dto.AdminUserDTO;
import com.example.user_service.dto.ChangePasswordRequestDTO;
import com.example.user_service.dto.UpdateUserDTO;
import com.example.user_service.dto.UserDTO;
import com.example.user_service.entity.UserRole;
import com.example.user_service.service.KeycloakService;
import com.example.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final KeycloakService keycloakService;

    // cauta un user dupa keycloakId
    // folosit in:
    // - api-gateway (SearchTerapeutService & ProfileService)
    // - programari-service (UserClient)
    @GetMapping("/by-keycloak/{keycloakId}")
    public ResponseEntity<UserDTO> getUserByKeycloakId(@PathVariable("keycloakId") String keycloakId) {
        return ResponseEntity.ok(userService.getUserByKeycloakId(keycloakId));
    }

    // cauta un user dupa id
    // folosit in:
    // - programari-service (UserClient)
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // cauta mai multi useri dintr-odata (rezolva problema retelei N+1)
    // folosit in:
    // - api-gateway sau alte servicii care au nevoie de mai multi useri simultan
    @PostMapping("/batch")
    public ResponseEntity<List<UserDTO>> getUsersInBatch(@RequestBody List<String> keycloakIds) {
        return ResponseEntity.ok(userService.getUsersByKeycloakIds(keycloakIds));
    }

    // cauta mai multi useri dupa DB ID
    @PostMapping("/batch/ids")
    public ResponseEntity<List<UserDTO>> getUsersInBatchByIds(@RequestBody List<Long> ids) {
        return ResponseEntity.ok(userService.getUsersByIds(ids));
    }

    // actualizeaza un user
    // folosit in:
    // - api-gateway (ProfileService)
    @PatchMapping("/{keycloakId}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable String keycloakId,
            @Valid @RequestBody UpdateUserDTO updateDTO) {
        return ResponseEntity.ok(userService.updateUser(keycloakId, updateDTO));
    }

    // schimba parola utilizatorului autentificat
    // keycloakId este extras direct din token-ul JWT (nu din path)
    @PutMapping("/my-password")
    public ResponseEntity<Void> changeMyPassword(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ChangePasswordRequestDTO request) {
        String keycloakId = jwt.getSubject();
        keycloakService.updatePassword(keycloakId, request.newPassword());
        return ResponseEntity.noContent().build();
    }

    // --------------- ADMIN ---------------

    // dezactivare/reactivare cont
    // folosit in: admin UI
    @PatchMapping("/by-keycloak/{keycloakId}/toggle-active")
    public ResponseEntity<AdminUserDTO> toggleActive(@PathVariable String keycloakId) {
        return ResponseEntity.ok(userService.toggleUserActive(keycloakId));
    }

    // listing useri cu filtre optionale
    // folosit in: admin UI
    @GetMapping
    public ResponseEntity<List<AdminUserDTO>> getUsers(
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) Boolean active) {
        return ResponseEntity.ok(userService.getUsers(role, active));
    }

    @GetMapping("/auth/me")
    public ResponseEntity<UserDTO> getMe(@org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.oauth2.jwt.Jwt jwt) {
        String keycloakId = jwt.getSubject();
        return ResponseEntity.ok(userService.getUserByKeycloakId(keycloakId));
    }
}

