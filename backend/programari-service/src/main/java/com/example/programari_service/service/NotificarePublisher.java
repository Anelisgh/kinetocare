package com.example.programari_service.service;

import com.example.programari_service.client.PacientiClient;
import com.example.programari_service.client.TerapeutiClient;
import com.example.programari_service.config.RabbitMQConfig;
import com.example.programari_service.dto.NotificareEvent;
import com.example.programari_service.entity.Programare;
import com.example.programari_service.mapper.NotificareEventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificarePublisher {

    private final RabbitTemplate rabbitTemplate;
    private final NotificareEventMapper notificareEventMapper;
    private final TerapeutiClient terapeutiClient;
    private final PacientiClient pacientiClient;

    // ──────────────────────────── Notificari catre TERAPEUT ────────────────────────────

    public void programareNoua(Programare p) {
        String keycloakId = getKeycloakIdTerapeut(p.getTerapeutId());
        trimite("notificare.programare.noua", notificareEventMapper.toProgramareNoua(p, keycloakId));
    }

    public void evaluareInitialaNoua(Programare p) {
        String keycloakId = getKeycloakIdTerapeut(p.getTerapeutId());
        trimite("notificare.evaluare.initiala", notificareEventMapper.toEvaluareInitialaNoua(p, keycloakId));
    }

    public void programareAnulataDePacient(Programare p) {
        String keycloakId = getKeycloakIdTerapeut(p.getTerapeutId());
        trimite("notificare.programare.anulata.pacient", notificareEventMapper.toProgramareAnulataDePacient(p, keycloakId));
    }

    public void reevaluareNecesara(Programare p) {
        String keycloakId = getKeycloakIdTerapeut(p.getTerapeutId());
        trimite("notificare.reevaluare.necesara", notificareEventMapper.toReevaluareNecesara(p, keycloakId));
    }

    public void jurnalCompletat(Long terapeutId, Long pacientId, Long programareId) {
        String keycloakId = getKeycloakIdTerapeut(terapeutId);
        trimite("notificare.jurnal.completat", notificareEventMapper.toJurnalCompletat(pacientId, programareId, keycloakId));
    }

    // ──────────────────────────── Notificari catre PACIENT ────────────────────────────

    public void programareAnulataDeTerapeut(Programare p) {
        String keycloakId = getKeycloakIdPacient(p.getPacientId());
        trimite("notificare.programare.anulata.terapeut", notificareEventMapper.toProgramareAnulataDeTerapeut(p, keycloakId));
    }

    public void reminder24h(Programare p) {
        String keycloakId = getKeycloakIdPacient(p.getPacientId());
        trimite("notificare.reminder.24h", notificareEventMapper.toReminder24h(p, keycloakId));
    }

    public void reminder2h(Programare p) {
        String keycloakId = getKeycloakIdPacient(p.getPacientId());
        trimite("notificare.reminder.2h", notificareEventMapper.toReminder2h(p, keycloakId));
    }

    public void reminderJurnal(Programare p) {
        String keycloakId = getKeycloakIdPacient(p.getPacientId());
        trimite("notificare.reminder.jurnal", notificareEventMapper.toReminderJurnal(p, keycloakId));
    }

    public void reevaluareRecomandata(Programare p) {
        String keycloakId = getKeycloakIdPacient(p.getPacientId());
        trimite("notificare.reevaluare.recomandata", notificareEventMapper.toReevaluareRecomandata(p, keycloakId));
    }

    // ──────────────────────────── Helpers ────────────────────────────

    private void trimite(String routingKey, NotificareEvent event) {
        if (event.userKeycloakId() == null) {
            log.warn("Notificarea {} nu a fost trimisă: userKeycloakId este null.", event.tipNotificare());
            return;
        }
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, routingKey, event);
            log.info("Notificare trimisă: {} → userKeycloakId={}", event.tipNotificare(), event.userKeycloakId());
        } catch (Exception e) {
            log.error("Eroare la trimiterea notificării {}: {}", event.tipNotificare(), e.getMessage());
        }
    }

    private String getKeycloakIdTerapeut(Long terapeutId) {
        try {
            return terapeutiClient.getKeycloakIdByTerapeutId(terapeutId);
        } catch (Exception e) {
            log.warn("Eroare Feign: nu s-a putut obține keycloakId pentru terapeutId={}: {}", terapeutId, e.getMessage());
            return null;
        }
    }

    private String getKeycloakIdPacient(Long pacientId) {
        try {
            return pacientiClient.getPacientById(pacientId).keycloakId();
        } catch (Exception e) {
            log.warn("Eroare Feign: nu s-a putut obține keycloakId pentru pacientId={}: {}", pacientId, e.getMessage());
            return null;
        }
    }
}
