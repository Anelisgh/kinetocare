package com.example.user_service.controller;

import com.example.user_service.dto.AdminUserDTO;
import com.example.user_service.dto.UpdateUserDTO;
import com.example.user_service.dto.UserDTO;
import com.example.user_service.entity.UserRole;
import com.example.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

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

    // actualizeaza un user
    // folosit in:
    // - api-gateway (ProfileService)
    @PatchMapping("/{keycloakId}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable String keycloakId,
            @Valid @RequestBody UpdateUserDTO updateDTO) {
        return ResponseEntity.ok(userService.updateUser(keycloakId, updateDTO));
    }

    // --------------- ADMIN ---------------

    // dezactivare/reactivare cont
    // folosit in: admin UI
    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<AdminUserDTO> toggleActive(@PathVariable Long id) {
        return ResponseEntity.ok(userService.toggleUserActive(id));
    }

    // listing useri cu filtre optionale
    // folosit in: admin UI
    @GetMapping
    public ResponseEntity<List<AdminUserDTO>> getUsers(
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) Boolean active) {
        return ResponseEntity.ok(userService.getUsers(role, active));
    }
}

