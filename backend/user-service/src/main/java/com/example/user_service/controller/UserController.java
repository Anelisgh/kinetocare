package com.example.user_service.controller;

import com.example.user_service.dto.UpdateUserDTO;
import com.example.user_service.dto.UserDTO;
import com.example.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/by-keycloak/{keycloakId}")
    public ResponseEntity<UserDTO> getUserByKeycloakId(@PathVariable String keycloakId) {
        return ResponseEntity.ok(userService.getUserByKeycloakId(keycloakId));
    }

    @PatchMapping("/{keycloakId}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable String keycloakId,
            @Valid @RequestBody UpdateUserDTO updateDTO) {
        return ResponseEntity.ok(userService.updateUser(keycloakId, updateDTO));
    }
}
