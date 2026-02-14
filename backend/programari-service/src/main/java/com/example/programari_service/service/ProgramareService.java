package com.example.programari_service.service;

import com.example.programari_service.client.PacientiClient;
import com.example.programari_service.client.ServiciiClient;
import com.example.programari_service.client.TerapeutiClient;
import com.example.programari_service.client.UserClient;
import com.example.programari_service.dto.*;
import com.example.programari_service.entity.Evaluare;
import com.example.programari_service.entity.MotivAnulare;
import com.example.programari_service.entity.Programare;
import com.example.programari_service.entity.StatusProgramare;
import com.example.programari_service.mapper.ProgramareMapper;
import com.example.programari_service.repository.EvaluareRepository;
import com.example.programari_service.repository.ProgramareRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.example.programari_service.dto.CalendarProgramareDTO;
import com.example.programari_service.dto.LocatieDisponibilaDTO;
import com.example.programari_service.dto.UserDisplayCalendarDTO;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProgramareService {

    private final ProgramareRepository programareRepository;
    private final ProgramareMapper programareMapper;
    private final ServiciiClient serviciiClient;
    private final TerapeutiClient terapeutiClient;
    private final EvaluareRepository evaluareRepository;
    private final PacientiClient pacientiClient;
    private final UserClient userClient;

    // ia prima programare viitoare a pacientului
    public Optional<UrmatoareaProgramareDTO> getUrmatoareaProgramare(Long pacientId) {
        // cerem pagina 0 cu 1 element (LIMIT 1)
        List<Programare> lista = programareRepository.gasesteUrmatoareaProgramare(
                pacientId,
                PageRequest.of(0, 1));

        if (lista.isEmpty()) {
            return Optional.empty();
        }
        // luam primul element din lista
        return Optional.of(programareMapper.toUrmatoareaProgramareDTO(lista.getFirst()));
    }

    // creaza o noua programare
    public Programare creeazaProgramare(CreeazaProgramareRequest request) {
        // Calculam daca e prima intalnire
        long istoric = programareRepository.countProgramariActiveSauFinalizate(
                request.getPacientId(),
                request.getTerapeutId());
        boolean isPrimaIntalnire = (istoric == 0);

        // Determinam serviciul corect
        DetaliiServiciuDTO serviciuDeAplicat = determinaServiciulCorect(
                request.getPacientId());
        // Calculam ora de sfarsit pe baza serviciului det automat
        LocalTime oraSfarsit = request.getOraInceput().plusMinutes(serviciuDeAplicat.getDurataMinute());

        // Verificam suprapuneri
        boolean eOcupat = programareRepository.existaSuprapunere(
                request.getTerapeutId(),
                request.getData(),
                request.getOraInceput(),
                oraSfarsit);

        if (eOcupat) {
            throw new RuntimeException("Intervalul orar este deja ocupat.");
        }

        Programare programareNoua = programareMapper.toEntity(
                request,
                serviciuDeAplicat,
                oraSfarsit,
                isPrimaIntalnire);

        return programareRepository.save(programareNoua);
    }

    // anuleaza o programare de catre pacient
    public void anuleazaProgramare(Long programareId, Long pacientId) {
        Programare programare = programareRepository.findById(programareId)
                .orElseThrow(() -> new RuntimeException("Programarea nu există"));

        // verifica ownership
        if (!programare.getPacientId().equals(pacientId)) {
            throw new RuntimeException("Nu aveți dreptul să anulați această programare");
        }
        // verifica daca programarea este programata
        if (programare.getStatus() != StatusProgramare.PROGRAMATA) {
            throw new RuntimeException("Doar programările programate pot fi anulate");
        }
        // setam statusul si motivul de anulare
        programare.setStatus(StatusProgramare.ANULATA);
        programare.setMotivAnulare(MotivAnulare.ANULAT_DE_PACIENT);

        programareRepository.save(programare);
    }

    // marcheaza neprezentarea de catre terapeut
    public void marcheazaNeprezentare(Long programareId, Long terapeutId, boolean isAdmin) {
        Programare programare = programareRepository.findById(programareId)
                .orElseThrow(() -> new RuntimeException("Programarea nu există"));

        // validari
        if (!isAdmin) {
            // daca nu e admin, aplicam restrictia -> terapeutul trebuie sa fie cel din
            // programare
            if (terapeutId == null || !programare.getTerapeutId().equals(terapeutId)) {
                throw new RuntimeException("Nu aveți dreptul să modificați această programare (nu vă aparține).");
            }
        }
        // Daca e admin, sarim peste verificarea de id (adminul poate modifica orice)

        // validare status
        if (programare.getStatus() == StatusProgramare.ANULATA) {
            throw new RuntimeException("Programarea este deja anulată.");
        }

        // modificam status si motiv anulare
        programare.setStatus(StatusProgramare.ANULATA);
        programare.setMotivAnulare(MotivAnulare.NEPREZENTARE);

        programareRepository.save(programare);
    }

    // logica serviciului corect pentru programari
    public DetaliiServiciuDTO determinaServiciulCorect(Long pacientId) {
        // cautam ultima evaluare a pacientului
        Optional<Evaluare> evaluareOpt = evaluareRepository.findFirstByPacientIdOrderByDataDesc(pacientId);

        // daca nu exista nici o evaluare -> EVALUARE
        if (evaluareOpt.isEmpty()) {
            return serviciiClient.gasesteServiciuDupaNume("Evaluare");
        }

        Evaluare evaluare = evaluareOpt.get();

        // numaram toate sedintele finalizate de pacient de la data evalaurii
        long sedinteEfectuateTotal = programareRepository.countSedintePacientDupaData(
                pacientId,
                evaluare.getData());

        // daca a efectuat toate sedintele recomandate in evaluare -> EVALUARE
        // daca nu -> SERVICIUL RECOMANDAT IN EVALUARE
        if (sedinteEfectuateTotal >= evaluare.getSedinteRecomandate()) {
            return serviciiClient.gasesteServiciuDupaNume("Evaluare");
        } else {
            return serviciiClient.getServiciuById(evaluare.getServiciuRecomandatId());
        }
    }

    // calculam sloturile orare libere
    public List<LocalTime> getSloturiDisponibile(Long terapeutId, Long locatieId, LocalDate data, Long serviciuId) {
        // verificam concediul
        Boolean inConcediu = terapeutiClient.checkConcediu(terapeutId, data.toString());
        if (Boolean.TRUE.equals(inConcediu)) {
            return List.of();
        }

        // obtinem disponibilitatea pentru ziua respectiva
        int ziSaptamana = data.getDayOfWeek().getValue();

        DisponibilitateDTO orar;
        try {
            orar = terapeutiClient.getOrar(terapeutId, locatieId, ziSaptamana);
        } catch (Exception e) {
            return List.of(); // daca nu lucreaza in ziua respectiva returnam lista goala
        }

        if (orar == null)
            return List.of();

        // obtinem durata serviciu
        DetaliiServiciuDTO serviciu = serviciiClient.getServiciuById(serviciuId);
        int durataMinute = serviciu.getDurataMinute();

        // obtinem lista de programari existente (ocupate)
        List<Programare> programariExistente = programareRepository.findByTerapeutIdAndDataAndStatus(
                terapeutId, data, StatusProgramare.PROGRAMATA);

        // generam sloturile libere
        List<LocalTime> sloturiLibere = new ArrayList<>();

        LocalTime cursor = orar.getOraInceput();
        LocalTime limitaSfarsit = orar.getOraSfarsit();

        // parcurgem intervalul orar si verificam daca mai este loc de inca o sedinta
        // (continuam atata timp cat finalul unui potential slot NU depaseste limita)
        while (!cursor.plusMinutes(durataMinute).isAfter(limitaSfarsit)) {
            LocalTime finalSlot = cursor.plusMinutes(durataMinute);

            // verificam daca slotul curent este liber avand in vedere programarile
            // existente
            if (esteLiber(cursor, finalSlot, programariExistente)) {
                // nu aratam sloturile din trecut
                if (!data.isEqual(LocalDate.now()) || cursor.isAfter(LocalTime.now())) {
                    sloturiLibere.add(cursor);
                }
            }

            // Avem 2 optiuni
            // OPTIUNEA 1: unul dupa altul, fara pauza pentru terapeut
            // cursor = cursor.plusMinutes(durataMinute);

            // OPTIUNEA 2: flexibilitate pentru terapeut, pauza de 10min intre pacienti
            cursor = cursor.plusMinutes(durataMinute + 10);
        }

        return sloturiLibere;
    }

    // helper pentru coliziuni
    // verifica daca un slot se suprapune cu vreo programare existenta
    private boolean esteLiber(LocalTime startNou, LocalTime endNou, List<Programare> existente) {
        for (Programare p : existente) {
            if (startNou.isBefore(p.getOraSfarsit()) && endNou.isAfter(p.getOraInceput())) {
                return false; // se suprapune
            }
        }
        return true;
    }

    // calendarul vizibil terapeutului
    public List<CalendarProgramareDTO> getCalendarAppointments(Long terapeutId, LocalDate start, LocalDate end, Long locatieId) {
        // luam programarile din baza de date
        List<Programare> programari = programareRepository.findAllByTerapeutIdAndDataBetween(terapeutId, start, end);

        // filtrarea logica
        // pastram programarea daca:
        // a. nu este anulata (e programata sau finalizata)
        // sau
        // b. este anulata, dar motivul este neprezentare
        programari = programari.stream()
                .filter(p -> p.getStatus() != StatusProgramare.ANULATA ||
                        p.getMotivAnulare() == MotivAnulare.NEPREZENTARE)
                .toList();

        // filtram dupa locatie (daca e selectata)
        if (locatieId != null) {
            programari = programari.stream()
                    .filter(p -> p.getLocatieId().equals(locatieId))
                    .toList();
        }

        // mapam folosind mapper-ul
        return programari.stream().map(p -> {
            String numePacient = "Necunoscut";
            String telefonPacient = "-";

            try {
                // pacient_id = user.id, deci folosim user-service
                UserDisplayCalendarDTO userDTO = userClient.getUserById(p.getPacientId());
                if (userDTO != null) {
                    numePacient = userDTO.getNume() + " " + userDTO.getPrenume();
                    telefonPacient = userDTO.getTelefon();
                }
            } catch (Exception e) {
                log.error("Eroare date externe pt programarea {}: {}", p.getId(), e.getMessage());
            }
            return programareMapper.toCalendarDTO(p, numePacient, telefonPacient, getNumeLocatie(p.getLocatieId()));
        }).toList();
    }

    // anulare de terapeut
    public void anuleazaProgramareTerapeut(Long programareId, Long terapeutId) {
        Programare programare = programareRepository.findById(programareId)
                .orElseThrow(() -> new RuntimeException("Programarea nu există"));

        // Terapeutul trebuie sa fie cel din programare
        if (!programare.getTerapeutId().equals(terapeutId)) {
            throw new RuntimeException("Nu aveți dreptul să anulați această programare (nu vă aparține).");
        }

        // validare
        if (programare.getStatus() != StatusProgramare.PROGRAMATA) {
            throw new RuntimeException("Doar programările viitoare pot fi anulate.");
        }

        // modificam status si motiv anulare
        programare.setStatus(StatusProgramare.ANULATA);
        programare.setMotivAnulare(MotivAnulare.ANULAT_DE_TERAPEUT);

        // TODO: notificare catre pacient

        programareRepository.save(programare);
    }

    // Cron Job pentru finalizarea automata a sedintelor
    // Ruleaza la fiecare 5 minute
    // gaseste programarile programate cu data si ora in trecut si le seteaza statusul finalizata
    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void finalizeazaProgramariExpirate() {
        LocalDate azi = LocalDate.now();
        LocalTime acum = LocalTime.now();

        List<Programare> expirate = programareRepository.findExpiredAppointments(azi, acum);

        if (!expirate.isEmpty()) {
            expirate.forEach(p -> p.setStatus(StatusProgramare.FINALIZATA));
            programareRepository.saveAll(expirate);
            log.info("Cron Job: S-au finalizat automat {} programări.", expirate.size());
        }
    }

    // sedinte finalizate fara jurnal
    public List<ProgramareJurnalDTO> getProgramariFaraJurnal(Long pacientId) {
        List<Programare> programari = programareRepository.findByPacientIdAndStatusAndAreJurnalFalseOrderByDataDesc(
                pacientId, StatusProgramare.FINALIZATA);

        return programari.stream().map(p -> {
            String numeTerapeut = getNumeTerapeut(p.getTerapeutId());
            String numeLocatie = getNumeLocatie(p.getLocatieId());
            return programareMapper.toProgramareJurnalDTO(p, numeTerapeut, numeLocatie);
        }).toList();
    }

    // seteaza areJurnal=true
    public void marcheazaProgramareCuJurnal(Long programareId) {
        Programare p = programareRepository.findById(programareId).orElseThrow();
        p.setAreJurnal(true);
        programareRepository.save(p);
    }

    // detaliile complete
    public ProgramareJurnalDTO getDetaliiProgramare(Long programareId) {
        Programare p = programareRepository.findById(programareId)
                .orElseThrow(() -> new RuntimeException("Programarea nu a fost găsită"));

        String numeTerapeut = getNumeTerapeut(p.getTerapeutId());
        String numeLocatie = getNumeLocatie(p.getLocatieId());

        return programareMapper.toProgramareJurnalDTO(p, numeTerapeut, numeLocatie);
    }

    // HELPER: obtine numele complet al terapeutului dupa terapeutId
    private String getNumeTerapeut(Long terapeutId) {
        try {
            String terapeutKeycloakId = terapeutiClient.getKeycloakIdByTerapeutId(terapeutId);
            if (terapeutKeycloakId != null) {
                UserDisplayCalendarDTO userTerapeut = userClient.getUserByKeycloakId(terapeutKeycloakId);
                if (userTerapeut != null) {
                    return userTerapeut.getNume() + " " + userTerapeut.getPrenume();
                }
            }
        } catch (Exception e) {
            log.error("Nu s-a putut prelua numele terapeutului {}: {}", terapeutId, e.getMessage());
        }
        return "Terapeut";
    }

    // HELPER: obtine numele locatiei dupa locatieId
    private String getNumeLocatie(Long locatieId) {
        try {
            LocatieDisponibilaDTO locatie = terapeutiClient.getLocatieById(locatieId);
            if (locatie != null) {
                return locatie.getNume();
            }
        } catch (Exception e) {
            log.error("Nu s-a putut prelua numele locatiei {}: {}", locatieId, e.getMessage());
        }
        return "Locație Necunoscută";
    }
}
