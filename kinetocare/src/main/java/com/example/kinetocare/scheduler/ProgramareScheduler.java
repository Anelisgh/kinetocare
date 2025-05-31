package com.example.kinetocare.scheduler;

import com.example.kinetocare.domain.Plata;
import com.example.kinetocare.domain.Programare;
import com.example.kinetocare.domain.StarePlata;
import com.example.kinetocare.domain.Status;
import com.example.kinetocare.repository.PlataRepository;
import com.example.kinetocare.repository.ProgramareRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProgramareScheduler {

    private final ProgramareRepository programareRepository;
    private final PlataRepository plataRepository;

    @Scheduled(cron = "0 0 2 * * *") // Ruleaza zilnic la ora 2 dimineața
    @Transactional
    public void actualizeazaStatusuriSiGenereazaPlati() {
        LocalDate azi = LocalDate.now();

        // finalizeaza programarile expirate
        List<Programare> deFinalizat = programareRepository
                .findByDataBeforeAndStatus(azi, Status.PROGRAMATA);

        deFinalizat.forEach(p -> {
            p.setStatus(Status.FINALIZATA);
            creazaPlataDacaNuExista(p);
        });

        programareRepository.saveAll(deFinalizat);

        // sterge programarile anulate vechi
        List<Programare> deSters = programareRepository
                .findByStatusAndDataBefore(Status.ANULATA, azi.minusDays(7));

        programareRepository.deleteAll(deSters);
    }

// SE CREEAZA PLATA AUTOMAT
//‧˚₊꒷꒦︶︶︶︶︶꒷꒦︶︶︶︶︶꒦꒷‧₊˚⊹
    private void creazaPlataDacaNuExista(Programare programare) {
        if (programare.getServiciu() == null || plataRepository.existsByProgramare(programare)) {
            return;
        }

        Plata plata = Plata.builder()
                .data(LocalDate.now())
                .suma(programare.getServiciu().getPret())
                .starePlata(StarePlata.IN_ASTEPTARE)
                .programare(programare)
                .pacient(programare.getPacient())
                .build();

        plataRepository.save(plata);
    }
}
