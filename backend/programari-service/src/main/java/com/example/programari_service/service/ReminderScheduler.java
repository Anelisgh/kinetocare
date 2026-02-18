package com.example.programari_service.service;

import com.example.programari_service.entity.Programare;
import com.example.programari_service.repository.ProgramareRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReminderScheduler {

    private final ProgramareRepository programareRepository;
    private final NotificarePublisher notificarePublisher;

    // Reminder 24h - ruleaza la fiecare 30 minute
    @Scheduled(cron = "0 */30 * * * *")
    public void trimiteRemindere24h() {
        List<Programare> programari = gasesteInFereastra(24, 15);

        if (!programari.isEmpty()) {
            programari.forEach(notificarePublisher::reminder24h);
            log.info("Reminder 24h: trimise {} notificări.", programari.size());
        }
    }

    // Reminder 2h - ruleaza la fiecare 15 minute
    @Scheduled(cron = "0 */15 * * * *")
    public void trimiteRemindere2h() {
        List<Programare> programari = gasesteInFereastra(2, 8);

        if (!programari.isEmpty()) {
            programari.forEach(notificarePublisher::reminder2h);
            log.info("Reminder 2h: trimise {} notificări.", programari.size());
        }
    }

    // gaseste programarile intr-o fereastra de timp
    // gestioneaza si cazul in care fereastra trece peste miezul noptii (ex: 23:50 + 24h = 23:50 maine ± 15m)
    private List<Programare> gasesteInFereastra(int oreInainte, int marjaMinute) {
        LocalDateTime acum = LocalDateTime.now();
        LocalDateTime centruFereastra = acum.plusHours(oreInainte);
        LocalDateTime startFereastra = centruFereastra.minusMinutes(marjaMinute);
        LocalDateTime endFereastra = centruFereastra.plusMinutes(marjaMinute);

        // caz normal: start si end sunt in aceeasi zi
        if (startFereastra.toLocalDate().equals(endFereastra.toLocalDate())) {
            return programareRepository.findProgramariInFereastra(
                    startFereastra.toLocalDate(),
                    startFereastra.toLocalTime(),
                    endFereastra.toLocalTime());
        }
 
        // caz limita: fereastra trece peste miezul noptii (ex: 23:50-00:10)
        // splitam in 2 query-uri: [23:50-23:59] in ziua 1 si [00:00-00:10] in ziua 2
        List<Programare> rezultat = new ArrayList<>();
        rezultat.addAll(programareRepository.findProgramariInFereastra(
                startFereastra.toLocalDate(),
                startFereastra.toLocalTime(),
                LocalTime.of(23, 59)));
        rezultat.addAll(programareRepository.findProgramariInFereastra(
                endFereastra.toLocalDate(),
                LocalTime.of(0, 0),
                endFereastra.toLocalTime()));
        return rezultat;
    }
}
