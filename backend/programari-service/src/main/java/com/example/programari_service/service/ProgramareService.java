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
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.example.programari_service.dto.CalendarProgramareDTO;
import com.example.programari_service.dto.LocatieDisponibilaDTO;
import com.example.programari_service.dto.UserDisplayCalendarDTO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public void anuleazaProgramare(Long programareId, Long pacientId) {
        Programare programare = programareRepository.findById(programareId)
                .orElseThrow(() -> new RuntimeException("Programarea nu există"));

        if (!programare.getPacientId().equals(pacientId)) {
            throw new RuntimeException("Nu aveți dreptul să anulați această programare");
        }

        if (programare.getStatus() != StatusProgramare.PROGRAMATA) {
            throw new RuntimeException("Doar programările programate pot fi anulate");
        }

        programare.setStatus(StatusProgramare.ANULATA);
        programare.setMotivAnulare(MotivAnulare.ANULAT_DE_PACIENT);

        programareRepository.save(programare);
    }

    public void marcheazaNeprezentare(Long programareId, Long terapeutId, boolean isAdmin) {
        Programare programare = programareRepository.findById(programareId)
                .orElseThrow(() -> new RuntimeException("Programarea nu există"));

        // 1. Validare Permisiuni
        if (!isAdmin) {
            // Dacă NU e admin, aplicăm restricția strictă: terapeutul trebuie să fie cel
            // din programare
            if (terapeutId == null || !programare.getTerapeutId().equals(terapeutId)) {
                throw new RuntimeException("Nu aveți dreptul să modificați această programare (nu vă aparține).");
            }
        }
        // Dacă e Admin, sărim peste verificarea de ID (Adminul poate modifica orice)

        // 2. Validare Status (Rămâne la fel)
        if (programare.getStatus() == StatusProgramare.ANULATA) {
            throw new RuntimeException("Programarea este deja anulată.");
        }

        // 3. Modificare
        programare.setStatus(StatusProgramare.ANULATA);
        programare.setMotivAnulare(MotivAnulare.NEPREZENTARE);

        programareRepository.save(programare);
    }

    // LOGICA SERVICIULUI CORECT PENTRU PROGRAMARI
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
    private boolean esteLiber(LocalTime startNou, LocalTime endNou, List<Programare> existente) {
        for (Programare p : existente) {
            if (startNou.isBefore(p.getOraSfarsit()) && endNou.isAfter(p.getOraInceput())) {
                return false; // se suprapune
            }
        }
        return true;
    }

    // PENTRU CALENDARUL VIZIBIL TERAPEUTULUI
    public List<CalendarProgramareDTO> getCalendarAppointments(Long terapeutId, LocalDate start, LocalDate end,
            Long locatieId) {
        // 1. Luăm programările din baza de date
        List<Programare> programari = programareRepository.findAllByTerapeutIdAndDataBetween(terapeutId, start, end);

        // 2. FILTRAREA LOGICĂ (Modificată)
        // Păstrăm programarea dacă:
        // A. NU este anulată (e Programată sau Finalizată)
        // SAU
        // B. Este anulată, DAR motivul este NEPREZENTARE (pe astea vrem să le vedem)
        programari = programari.stream()
                .filter(p -> p.getStatus() != StatusProgramare.ANULATA ||
                        p.getMotivAnulare() == MotivAnulare.NEPREZENTARE)
                .toList();

        // 3. Filtrăm după locație (dacă e selectată) - Rămâne la fel
        if (locatieId != null) {
            programari = programari.stream()
                    .filter(p -> p.getLocatieId().equals(locatieId))
                    .toList();
        }

        // 4. Mapăm folosind Mapper-ul - Rămâne la fel
        return programari.stream().map(p -> {
            String numePacient = "Necunoscut";
            String telefonPacient = "-";
            String numeLocatie = "Locație Necunoscută";

            try {
                // pacient_id in programari = user.id, so we call user-service directly
                UserDisplayCalendarDTO userDTO = userClient.getUserById(p.getPacientId());
                if (userDTO != null) {
                    numePacient = userDTO.getNume() + " " + userDTO.getPrenume();
                    telefonPacient = userDTO.getTelefon();
                }

                // C. Obținem Numele Locației
                LocatieDisponibilaDTO locatieDTO = terapeutiClient.getLocatieById(p.getLocatieId());
                if (locatieDTO != null) {
                    numeLocatie = locatieDTO.getNume();
                }

            } catch (Exception e) {
                System.err.println("Eroare date externe pt programarea " + p.getId() + ": " + e.getMessage());
            }
            return programareMapper.toCalendarDTO(p, numePacient, telefonPacient, numeLocatie);
        }).toList();
    }

    public void anuleazaProgramareTerapeut(Long programareId, Long terapeutId) {
        Programare programare = programareRepository.findById(programareId)
                .orElseThrow(() -> new RuntimeException("Programarea nu există"));

        // Terapeutul trebuie sa fie cel din programare
        if (!programare.getTerapeutId().equals(terapeutId)) {
            throw new RuntimeException("Nu aveți dreptul să anulați această programare (nu vă aparține).");
        }

        // Validare: Status
        if (programare.getStatus() != StatusProgramare.PROGRAMATA) {
            throw new RuntimeException("Doar programările viitoare pot fi anulate.");
        }

        programare.setStatus(StatusProgramare.ANULATA);
        programare.setMotivAnulare(MotivAnulare.ANULAT_DE_TERAPEUT);

        // TODO: notificare către pacient

        programareRepository.save(programare);
    }

    // Cron Job pentru finalizarea automată a ședințelor
    // Rulează la fiecare 5 minute
    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void finalizeazaProgramariExpirate() {
        LocalDate azi = LocalDate.now();
        LocalTime acum = LocalTime.now();

        List<Programare> expirate = programareRepository.findExpiredAppointments(azi, acum);

        if (!expirate.isEmpty()) {
            expirate.forEach(p -> p.setStatus(StatusProgramare.FINALIZATA));
            programareRepository.saveAll(expirate);
            System.out.println("Cron Job: S-au finalizat automat " + expirate.size() + " programări.");
        }
    }

    public List<ProgramareJurnalDTO> getProgramariFaraJurnal(Long pacientId) {
        List<Programare> programari = programareRepository.findByPacientIdAndStatusAndAreJurnalFalseOrderByDataDesc(
                pacientId, StatusProgramare.FINALIZATA);

        return programari.stream().map(p -> {
            String numeTerapeut = "Terapeut";
            String numeLocatie = "Locație Necunoscută";

            // A. Extragem Numele Terapeutului: terapeut_id → keycloak_id → user name
            try {
                // 1. Obținem keycloakId din terapeuti-service
                String terapeutKeycloakId = terapeutiClient.getKeycloakIdByTerapeutId(p.getTerapeutId());
                if (terapeutKeycloakId != null) {
                    // 2. Folosim keycloakId pentru a căuta user-ul
                    UserDisplayCalendarDTO userTerapeut = userClient.getUserByKeycloakId(terapeutKeycloakId);
                    if (userTerapeut != null) {
                        numeTerapeut = userTerapeut.getNume() + " " + userTerapeut.getPrenume();
                    }
                }
            } catch (Exception e) {
                System.err.println("Nu s-a putut prelua numele terapeutului: " + e.getMessage());
            }

            // B. Extragem Locația (Rămâne prin TerapeutiClient, că locația e specifică)
            try {
                LocatieDisponibilaDTO locatie = terapeutiClient.getLocatieById(p.getLocatieId());
                if (locatie != null) {
                    numeLocatie = locatie.getNume();
                }
            } catch (Exception e) {
                // ignoram eroarea de locatie
            }

            return ProgramareJurnalDTO.builder()
                    .id(p.getId())
                    .tipServiciu(p.getTipServiciu())
                    .data(p.getData())
                    .ora(p.getOraInceput())
                    .numeTerapeut(numeTerapeut)
                    .numeLocatie(numeLocatie)
                    .build();
        }).toList();
    }

    public void marcheazaProgramareCuJurnal(Long programareId) {
        Programare p = programareRepository.findById(programareId).orElseThrow();
        p.setAreJurnal(true);
        programareRepository.save(p);
    }

    public ProgramareJurnalDTO getDetaliiProgramare(Long programareId) {
        Programare p = programareRepository.findById(programareId)
                .orElseThrow(() -> new RuntimeException("Programarea nu a fost găsită"));

        String numeTerapeut = "Terapeut";
        String numeLocatie = "Locație";

        try {
            // 1. Obținem keycloakId din terapeuti-service
            String terapeutKeycloakId = terapeutiClient.getKeycloakIdByTerapeutId(p.getTerapeutId());
            if (terapeutKeycloakId != null) {
                // 2. Folosim keycloakId pentru a căuta user-ul
                UserDisplayCalendarDTO userTerapeut = userClient.getUserByKeycloakId(terapeutKeycloakId);
                if (userTerapeut != null) {
                    numeTerapeut = userTerapeut.getNume() + " " + userTerapeut.getPrenume();
                }
            }
        } catch (Exception e) {
            // fallback la default
        }

        try {
            LocatieDisponibilaDTO locatie = terapeutiClient.getLocatieById(p.getLocatieId());
            if (locatie != null) {
                numeLocatie = locatie.getNume();
            }
        } catch (Exception e) {
            // fallback la default
        }

        return ProgramareJurnalDTO.builder()
                .id(p.getId())
                .tipServiciu(p.getTipServiciu())
                .data(p.getData())
                .ora(p.getOraInceput())
                .numeTerapeut(numeTerapeut)
                .numeLocatie(numeLocatie)
                .build();
    }
}
