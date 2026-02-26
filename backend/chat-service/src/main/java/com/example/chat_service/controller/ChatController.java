package com.example.chat_service.controller;

import com.example.chat_service.dto.ConversatieDTO;
import com.example.chat_service.dto.MesajDTO;
import com.example.chat_service.entity.TipExpeditor;
import com.example.chat_service.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat/conversatii")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<List<ConversatieDTO>> getConversations(
            @RequestParam String userKeycloakId,
            @RequestParam TipExpeditor tipUser) {
        return ResponseEntity.ok(chatService.obtineConversatii(userKeycloakId, tipUser));
    }

    @PostMapping
    public ResponseEntity<ConversatieDTO> creeazaSauObtineConversatie(
            @RequestParam String pacientKeycloakId,
            @RequestParam String terapeutKeycloakId) {
        return ResponseEntity.ok(chatService.creeazaSauObtineConversatie(pacientKeycloakId, terapeutKeycloakId));
    }

    @GetMapping("/{conversatieId}/mesaje")
    public ResponseEntity<List<MesajDTO>> getMessages(@PathVariable Long conversatieId) {
        return ResponseEntity.ok(chatService.obtineMesajeDinConversatie(conversatieId));
    }

    @PutMapping("/{conversatieId}/citit")
    public ResponseEntity<Void> markMessagesAsRead(
            @PathVariable Long conversatieId,
            @RequestParam String userKeycloakId,
            @RequestParam TipExpeditor tipUser) {
        chatService.marcheazaMesajeleCaCitite(conversatieId, userKeycloakId, tipUser);
        return ResponseEntity.noContent().build();
    }
}
