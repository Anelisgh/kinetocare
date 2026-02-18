package com.example.notificari_service.service;

import com.example.notificari_service.dto.NotificareEvent;
import com.example.notificari_service.entity.Notificare;
import com.example.notificari_service.entity.TipNotificare;
import com.example.notificari_service.entity.TipUser;
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

    // proceseaza evenimentul primit din RabbitMQ si salveaza notificarea in DB
    @Transactional
    public void proceseazaEveniment(NotificareEvent event) {
        Notificare notificare = Notificare.builder()
                .userId(event.getUserId())
                .tipUser(TipUser.valueOf(event.getTipUser()))
                .tip(TipNotificare.valueOf(event.getTipNotificare()))
                .titlu(event.getTitlu())
                .mesaj(event.getMesaj())
                .entitateLegataId(event.getEntitateLegataId())
                .tipEntitateLegata(event.getTipEntitateLegata())
                .urlActiune(event.getUrlActiune())
                .esteCitita(false)
                .build();

        notificareRepository.save(notificare);
        log.info("Notificare salvată: {} → userId={}", event.getTipNotificare(), event.getUserId());
    }

    // returneaza notificarile unui user
    public List<Notificare> getNotificari(Long userId, TipUser tipUser) {
        return notificareRepository.findByUserIdAndTipUserOrderByCreatedAtDesc(userId, tipUser);
    }

    // marcheaza o notificare ca citita
    @Transactional
    public void marcheazaCitita(Long notificareId) {
        Notificare notificare = notificareRepository.findById(notificareId)
                .orElseThrow(() -> new RuntimeException("Notificarea nu a fost găsită"));
        notificare.setEsteCitita(true);
        notificare.setCititaLa(OffsetDateTime.now());
        notificareRepository.save(notificare);
    }

    // numara notificarile necitite
    public long getNumarNecitite(Long userId, TipUser tipUser) {
        return notificareRepository.countByUserIdAndTipUserAndEsteCititaFalse(userId, tipUser);
    }

    // marcheaza toate notificarile necitite ca citite
    @Transactional
    public void marcheazaToateCitite(Long userId, TipUser tipUser) {
        List<Notificare> necitite = notificareRepository.findByUserIdAndTipUserAndEsteCititaFalse(userId, tipUser);
        OffsetDateTime acum = OffsetDateTime.now();
        necitite.forEach(n -> {
            n.setEsteCitita(true);
            n.setCititaLa(acum);
        });
        notificareRepository.saveAll(necitite);
        log.info("Marcate {} notificări ca citite pentru userId={}", necitite.size(), userId);
    }
}
