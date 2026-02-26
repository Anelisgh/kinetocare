package com.example.chat_service.controller;

import com.example.chat_service.dto.MesajDTO;
import com.example.chat_service.dto.TrimitereMesajRequest;
import com.example.chat_service.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void trimiteMesaj(@Payload @Valid TrimitereMesajRequest request) {
        log.info("Mesaj WebSocket primit pentru conversatia: {}", request.conversatieId());

        MesajDTO mesajSalvat = chatService.salveazaSiNotifica(request);

        // Determinăm ID-ul destinatarului
        // Momentan, ChatService -> save and notify ne dă MesajDTO, expeditorul e cunoscut
        // Ca să trimitem doar destinatarului, ne luăm ID-ul din interfață/client
        // Un mod curat este să mapăm destinatarii via `/user/{recipientId}/queue/mesaje`. 
        // Va trebui o convenție care să extragă corect destinatarul din conversatie.
        
        // În viața reală vom extrage ConversatieDTO să vedem cealaltă entitate, sau TrimiteMesajRequest ar putea conține
        // ID-ul destinatarului pt. performanță. Aici, din motive de simplitate, delegăm clientului să se aboneze corect 
        // sau broadcast pe un topic privat per conversatie: `/queue/conversatii/{conversatieId}`

        messagingTemplate.convertAndSend("/queue/conversatii/" + request.conversatieId(), mesajSalvat);
        log.info("Mesaj rutat pe broker la destinatia /queue/conversatii/{}", request.conversatieId());
    }
}
