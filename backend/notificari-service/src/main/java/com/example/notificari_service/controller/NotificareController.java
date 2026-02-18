package com.example.notificari_service.controller;

import com.example.notificari_service.entity.Notificare;
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

    // lista notificarile unui user
    @GetMapping
    public ResponseEntity<List<Notificare>> getNotificari(
            @RequestParam Long userId,
            @RequestParam TipUser tipUser) {
        return ResponseEntity.ok(notificareService.getNotificari(userId, tipUser));
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
            @RequestParam Long userId,
            @RequestParam TipUser tipUser) {
        long count = notificareService.getNumarNecitite(userId, tipUser);
        return ResponseEntity.ok(Map.of("count", count));
    }

    // marcheaza toate notificarile ca citite
    @PutMapping("/citite-toate")
    public ResponseEntity<Void> marcheazaToateCitite(
            @RequestParam Long userId,
            @RequestParam TipUser tipUser) {
        notificareService.marcheazaToateCitite(userId, tipUser);
        return ResponseEntity.ok().build();
    }
}
