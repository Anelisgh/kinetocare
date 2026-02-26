package com.example.chat_service.service;

import com.example.chat_service.dto.ConversatieDTO;
import com.example.chat_service.dto.MesajDTO;
import com.example.chat_service.dto.NotificareEvent;
import com.example.chat_service.dto.TrimitereMesajRequest;
import com.example.chat_service.entity.Conversatie;
import com.example.chat_service.entity.Mesaj;
import com.example.chat_service.entity.TipExpeditor;
import com.example.chat_service.exception.ResourceNotFoundException;
import com.example.chat_service.mapper.ChatMapper;
import com.example.chat_service.repository.ConversatieRepository;
import com.example.chat_service.repository.MesajRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.example.chat_service.client.ProgramariClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ConversatieRepository conversatieRepository;
    private final MesajRepository mesajRepository;
    private final ChatMapper chatMapper;
    private final RabbitTemplate rabbitTemplate;
    private final ProgramariClient programariClient;

    @Transactional(readOnly = true)
    public List<ConversatieDTO> obtineConversatii(String userKeycloakId, TipExpeditor tipUser) {
        log.info("Aducere conversatii pentru {} cu keycloakId: {}", tipUser, userKeycloakId);
        return conversatieRepository.findByUserKeycloakIdOrderByUltimulMesajLaDesc(userKeycloakId)
                .stream()
                .map(conversatie -> {
                    Mesaj ultimulMesaj = mesajRepository.findTopByConversatieIdOrderByTrimisLaDesc(conversatie.getId()).orElse(null);
                    return chatMapper.toConversatieDTOWithMesaj(conversatie, ultimulMesaj);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public ConversatieDTO creeazaSauObtineConversatie(String pacientKeycloakId, String terapeutKeycloakId) {
        log.info("Creare sau obtinere conversatie intre pacientul {} si terapeutul {}", pacientKeycloakId, terapeutKeycloakId);
        Conversatie conversatie = conversatieRepository
                .findByPacientKeycloakIdAndTerapeutKeycloakId(pacientKeycloakId, terapeutKeycloakId)
                .orElseGet(() -> {
                    Conversatie noua = Conversatie.builder()
                            .pacientKeycloakId(pacientKeycloakId)
                            .terapeutKeycloakId(terapeutKeycloakId)
                            .ultimulMesajLa(OffsetDateTime.now(ZoneOffset.UTC))
                            .build();
                    return conversatieRepository.save(noua);
                });

        Mesaj ultimulMesaj = mesajRepository.findTopByConversatieIdOrderByTrimisLaDesc(conversatie.getId()).orElse(null);
        return chatMapper.toConversatieDTOWithMesaj(conversatie, ultimulMesaj);
    }

    @Transactional(readOnly = true)
    public List<MesajDTO> obtineMesajeDinConversatie(Long conversatieId) {
        log.info("Aducere mesaje pentru conversatia: {}", conversatieId);
        // Validam daca exista conversatia
        conversatieRepository.findById(conversatieId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversația nu a putut fi găsită."));

        return mesajRepository.findByConversatieIdOrderByTrimisLaAsc(conversatieId)
                .stream()
                .map(chatMapper::toMesajDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void marcheazaMesajeleCaCitite(Long conversatieId, String userKeycloakId, TipExpeditor tipUser) {
        log.info("Marcare mesaje ca citite in conversatia {} pentru keycloakId: {}", conversatieId, userKeycloakId);
        conversatieRepository.findById(conversatieId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversația nu a putut fi găsită."));

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        int updatedMessages = mesajRepository.markMessagesAsRead(conversatieId, userKeycloakId, now);
        log.info("Au fost marcate ca citite {} mesaje", updatedMessages);
    }

    @Transactional
    public MesajDTO salveazaSiNotifica(TrimitereMesajRequest cerere) {
        log.info("Salvare mesaj nou de la expeditorKeycloakId: {}", cerere.expeditorKeycloakId());

        String pacientKeycloakId = (cerere.tipExpeditor() == TipExpeditor.PACIENT)
                ? cerere.expeditorKeycloakId() : cerere.destinatarKeycloakId();
        String terapeutKeycloakId = (cerere.tipExpeditor() == TipExpeditor.PACIENT)
                ? cerere.destinatarKeycloakId() : cerere.expeditorKeycloakId();

        // 1. Verificare de Securitate — relația terapeutică trebuie să fie activă
        Boolean isActiva = programariClient.getRelatieStatusByKeycloak(pacientKeycloakId, terapeutKeycloakId);
        if (isActiva == null || !isActiva) {
            log.warn("BLOCARE: Relația pac={} ter={} este INACTIVĂ!", pacientKeycloakId, terapeutKeycloakId);
            throw new com.example.chat_service.exception.ForbiddenOperationException(
                    "Nu poți trimite mesaje într-o conversație arhivată.");
        }

        // 2. Lazy Initialization — găsim sau creăm conversația
        Conversatie conversatie;
        if (cerere.conversatieId() != null) {
            conversatie = conversatieRepository.findById(cerere.conversatieId())
                    .orElseThrow(() -> new ResourceNotFoundException("Conversația " + cerere.conversatieId() + " nu a fost găsită."));
        } else {
            conversatie = conversatieRepository
                    .findByPacientKeycloakIdAndTerapeutKeycloakId(pacientKeycloakId, terapeutKeycloakId)
                    .orElseGet(() -> {
                        log.info("CREARE CONVERSAȚIE NOUĂ (Lazy Init) pac={} ter={}", pacientKeycloakId, terapeutKeycloakId);
                        Conversatie noua = Conversatie.builder()
                                .pacientKeycloakId(pacientKeycloakId)
                                .terapeutKeycloakId(terapeutKeycloakId)
                                .ultimulMesajLa(OffsetDateTime.now(ZoneOffset.UTC))
                                .build();
                        return conversatieRepository.save(noua);
                    });
        }

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        Mesaj mesajNou = Mesaj.builder()
                .conversatieId(conversatie.getId())
                .expeditorKeycloakId(cerere.expeditorKeycloakId())
                .tipExpeditor(cerere.tipExpeditor())
                .continut(cerere.continut())
                .esteCitit(false)
                .trimisLa(now)
                .build();

        mesajRepository.save(mesajNou);

        conversatie.setUltimulMesajLa(now);
        conversatieRepository.save(conversatie);

        trimiteNotificareSpreRabbitMQ(cerere, conversatie, now);

        return chatMapper.toMesajDTO(mesajNou);
    }

    private void trimiteNotificareSpreRabbitMQ(TrimitereMesajRequest cerere, Conversatie conversatie, OffsetDateTime now) {
        // Destinatarul notificarii e cel care NU a trimis mesajul
        String recipientKeycloakId = (cerere.tipExpeditor() == TipExpeditor.PACIENT)
                ? conversatie.getTerapeutKeycloakId()
                : conversatie.getPacientKeycloakId();
        String eventType = (cerere.tipExpeditor() == TipExpeditor.PACIENT)
                ? "MESAJ_DE_LA_PACIENT" : "MESAJ_DE_LA_TERAPEUT";
        String tipRecipient = (cerere.tipExpeditor() == TipExpeditor.PACIENT) ? "TERAPEUT" : "PACIENT";

        NotificareEvent event = NotificareEvent.builder()
                .tipNotificare(eventType)
                .userKeycloakId(recipientKeycloakId)
                .tipUser(tipRecipient)
                .titlu("Mesaj Nou")
                .mesaj("Ai primit un mesaj nou.")
                .entitateLegataId(conversatie.getId())
                .tipEntitateLegata("CONVERSATIE")
                .urlActiune("/chat/" + conversatie.getId())
                .build();

        rabbitTemplate.convertAndSend("notificari.exchange", "notificare.mesaj.nou", event);
        log.info("Notificare trimisa in RabbitMQ pentru destinatarul {} ({})", recipientKeycloakId, eventType);
    }
}
