package com.example.notificari_service.service;

import com.example.notificari_service.dto.NotificareDTO;
import com.example.notificari_service.dto.NotificareEvent;
import com.example.notificari_service.mapper.NotificareMapper;
import com.example.notificari_service.entity.Notificare;
import com.example.notificari_service.entity.TipNotificare;
import com.example.notificari_service.entity.TipUser;
import com.example.notificari_service.exception.ResourceNotFoundException;
import com.example.notificari_service.repository.NotificareRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificareService {

    private final NotificareRepository notificareRepository;
    private final NotificareMapper notificareMapper;

    // proceseaza evenimentul primit din RabbitMQ si salveaza notificarea in DB
    @Transactional
    public void proceseazaEveniment(NotificareEvent event) {
        Notificare notificare = Notificare.builder()
                .userId(event.userId())
                .tipUser(TipUser.valueOf(event.tipUser()))
                .tip(TipNotificare.valueOf(event.tipNotificare()))
                .titlu(event.titlu())
                .mesaj(event.mesaj())
                .entitateLegataId(event.entitateLegataId())
                .tipEntitateLegata(event.tipEntitateLegata())
                .urlActiune(event.urlActiune())
                .esteCitita(false)
                .build();

        notificareRepository.save(notificare);
        log.info("Notificare salvată: {} → userId={}", event.tipNotificare(), event.userId());
    }

    // returneaza notificarile unui user
    @Transactional(readOnly = true)
    public List<NotificareDTO> getNotificari(Long userId, TipUser tipUser) {
        return notificareRepository.findByUserIdAndTipUserOrderByCreatedAtDesc(userId, tipUser)
                .stream()
                .map(notificareMapper::toDto)
                .toList();
    }

    // marcheaza o notificare ca citita
    @Transactional
    public void marcheazaCitita(Long notificareId) {
        Notificare notificare = notificareRepository.findById(notificareId)
                .orElseThrow(() -> new ResourceNotFoundException("Notificarea nu a fost găsită"));
        notificare.setEsteCitita(true);
        notificare.setCititaLa(OffsetDateTime.now());
        notificareRepository.save(notificare);
    }

    // numara notificarile necitite
    @Transactional(readOnly = true)
    public long getNumarNecitite(Long userId, TipUser tipUser) {
        return notificareRepository.countByUserIdAndTipUserAndEsteCititaFalse(userId, tipUser);
    }

    // marcheaza toate notificarile necitite ca citite
    @Transactional
    public void marcheazaToateCitite(Long userId, TipUser tipUser) {
        int updatedCount = notificareRepository.markAllAsReadByUserIdAndTipUser(userId, tipUser);
        log.info("Marcate {} notificări ca citite pentru userId={}", updatedCount, userId);
    }
}
