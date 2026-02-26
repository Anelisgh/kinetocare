package com.example.notificari_service.controller;

import com.example.notificari_service.dto.NotificareDTO;
import com.example.notificari_service.entity.TipUser;
import com.example.notificari_service.service.NotificareService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notificari")
@RequiredArgsConstructor
public class NotificareController {

    private final NotificareService notificareService;

    // lista notificarile unui user dupa keycloakId
    @GetMapping
    public ResponseEntity<List<NotificareDTO>> getNotificari(
            @RequestParam String userKeycloakId,
            @RequestParam TipUser tipUser) {
        return ResponseEntity.ok(notificareService.getNotificari(userKeycloakId, tipUser));
    }

    // marcheaza o notificare ca citita
    @PutMapping("/{id}/citita")
    public ResponseEntity<Void> marcheazaCitita(@PathVariable Long id) {
        notificareService.marcheazaCitita(id);
        return ResponseEntity.ok().build();
    }

    // numarul de notificari necitite
    @GetMapping("/necitite/count")
    public ResponseEntity<Map<String, Long>> getNumarNecitite(
            @RequestParam String userKeycloakId,
            @RequestParam TipUser tipUser) {
        long count = notificareService.getNumarNecitite(userKeycloakId, tipUser);
        return ResponseEntity.ok(Map.of("count", count));
    }

    // marcheaza toate notificarile ca citite
    @PutMapping("/citite-toate")
    public ResponseEntity<Void> marcheazaToateCitite(
            @RequestParam String userKeycloakId,
            @RequestParam TipUser tipUser) {
        notificareService.marcheazaToateCitite(userKeycloakId, tipUser);
        return ResponseEntity.ok().build();
    }
}
