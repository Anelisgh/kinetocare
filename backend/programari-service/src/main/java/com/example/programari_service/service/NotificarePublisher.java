package com.example.programari_service.service;

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

    // Notificari catre TERAPEUT

    public void programareNoua(Programare p) {
        trimite("notificare.programare.noua", notificareEventMapper.toProgramareNoua(p));
    }

    public void evaluareInitialaNoua(Programare p) {
        trimite("notificare.evaluare.initiala", notificareEventMapper.toEvaluareInitialaNoua(p));
    }

    public void programareAnulataDePacient(Programare p) {
        trimite("notificare.programare.anulata.pacient", notificareEventMapper.toProgramareAnulataDePacient(p));
    }

    public void reevaluareNecesara(Programare p) {
        trimite("notificare.reevaluare.necesara", notificareEventMapper.toReevaluareNecesara(p));
    }

    public void jurnalCompletat(Long terapeutId, Long pacientId, Long programareId) {
        trimite("notificare.jurnal.completat", notificareEventMapper.toJurnalCompletat(terapeutId, pacientId, programareId));
    }

    // Notificari catre PACIENT

    public void programareAnulataDeTerapeut(Programare p) {
        trimite("notificare.programare.anulata.terapeut", notificareEventMapper.toProgramareAnulataDeTerapeut(p));
    }

    public void reminder24h(Programare p) {
        trimite("notificare.reminder.24h", notificareEventMapper.toReminder24h(p));
    }

    public void reminder2h(Programare p) {
        trimite("notificare.reminder.2h", notificareEventMapper.toReminder2h(p));
    }

    public void reminderJurnal(Programare p) {
        trimite("notificare.reminder.jurnal", notificareEventMapper.toReminderJurnal(p));
    }

    public void reevaluareRecomandata(Programare p) {
        trimite("notificare.reevaluare.recomandata", notificareEventMapper.toReevaluareRecomandata(p));
    }

    // Helper

    private void trimite(String routingKey, NotificareEvent event) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, routingKey, event);
            log.info("Notificare trimisă: {} → userId={}", event.tipNotificare(), event.userId());
        } catch (Exception e) {
            log.error("Eroare la trimiterea notificării {}: {}", event.tipNotificare(), e.getMessage());
        }
    }
}
