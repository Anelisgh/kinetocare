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
import com.example.programari_service.exception.*;
import com.example.programari_service.mapper.EvaluareMapper;
import com.example.programari_service.mapper.IstoricProgramareMapper;
import com.example.programari_service.mapper.ProgramareMapper;
import com.example.programari_service.repository.EvaluareRepository;
import com.example.programari_service.repository.ProgramareRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProgramareService {

    private final ProgramareRepository programareRepository;
    private final ProgramareMapper programareMapper;
    private final IstoricProgramareMapper istoricProgramareMapper;
    private final ServiciiClient serviciiClient;
    private final TerapeutiClient terapeutiClient;
    private final EvaluareRepository evaluareRepository;
    private final EvaluareMapper evaluareMapper;
    private final PacientiClient pacientiClient;
    private final UserClient userClient;
    private final NotificarePublisher notificarePublisher;
    private final RelatieService relatieService;

    @Value("${app.service-names.initial}")
    private String numeEvaluareInitiala;

    @Value("${app.service-names.recurring}")
    private String numeReevaluare;

    // ia prima programare viitoare a pacientului
    @Transactional(readOnly = true)
    public Optional<UrmatoareaProgramareDTO> getUrmatoareaProgramare(Long pacientId) {
        // cerem pagina 0 cu 1 element (LIMIT 1)
        List<Programare> lista = programareRepository.gasesteUrmatoareaProgramare(
                pacientId,
                LocalDate.now(),
                LocalTime.now(),
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
                request.pacientId(),
                request.terapeutId());
        boolean isPrimaIntalnire = (istoric == 0);

        // Determinam serviciul corect
        DetaliiServiciuDTO serviciuDeAplicat = determinaServiciulCorect(
                request.pacientId());
        // Calculam ora de sfarsit pe baza serviciului det automat
        LocalTime oraSfarsit = request.oraInceput().plusMinutes(serviciuDeAplicat.durataMinute());

        // Verificam suprapuneri
        boolean eOcupat = programareRepository.existaSuprapunere(
                request.terapeutId(),
                request.data(),
                request.oraInceput(),
                oraSfarsit);

        if (eOcupat) {
            throw new ResourceAlreadyExistsException("Intervalul orar este deja ocupat.");
        }

        Programare programareNoua = programareMapper.toEntity(
                request,
                serviciuDeAplicat,
                oraSfarsit,
                isPrimaIntalnire);

        Programare salvata = programareRepository.save(programareNoua);

        // notificari catre terapeut
        notificarePublisher.programareNoua(salvata);
        // notificam DOAR daca serviciul determinat este chiar "Evaluare Inițială"
        if (numeEvaluareInitiala.equalsIgnoreCase(serviciuDeAplicat.nume())) {
            notificarePublisher.evaluareInitialaNoua(salvata);
        }
        // daca serviciul ales automat e "Reevaluare", notificam terapeutul
        if (numeReevaluare.equalsIgnoreCase(serviciuDeAplicat.nume())) {
            notificarePublisher.reevaluareNecesara(salvata);
        }

        return salvata;
    }

    // anuleaza o programare de catre pacient
    public void anuleazaProgramare(Long programareId, Long pacientId) {
        Programare programare = programareRepository.findById(programareId)
                .orElseThrow(() -> new ResourceNotFoundException("Programarea nu există"));

        // verifica ownership
        if (!programare.getPacientId().equals(pacientId)) {
            throw new ForbiddenOperationException("Nu aveți dreptul să anulați această programare");
        }
        // verifica daca programarea este programata
        if (programare.getStatus() != StatusProgramare.PROGRAMATA) {
            throw new ForbiddenOperationException("Doar programările programate pot fi anulate");
        }
        // setam statusul si motivul de anulare
        programare.setStatus(StatusProgramare.ANULATA);
        programare.setMotivAnulare(MotivAnulare.ANULAT_DE_PACIENT);

        programareRepository.save(programare);

        // notificare catre terapeut
        notificarePublisher.programareAnulataDePacient(programare);
    }

    // marcheaza neprezentarea de catre terapeut
    public void marcheazaNeprezentare(Long programareId, Long terapeutId, boolean isAdmin) {
        Programare programare = programareRepository.findById(programareId)
                .orElseThrow(() -> new ResourceNotFoundException("Programarea nu există"));

        // validari
        if (!isAdmin) {
            // daca nu e admin, aplicam restrictia -> terapeutul trebuie sa fie cel din
            // programare
            if (terapeutId == null || !programare.getTerapeutId().equals(terapeutId)) {
                throw new ForbiddenOperationException("Nu aveți dreptul să modificați această programare (nu vă aparține).");
            }
        }
        // Daca e admin, sarim peste verificarea de id (adminul poate modifica orice)

        // validare status
        if (programare.getStatus() == StatusProgramare.ANULATA) {
            throw new ForbiddenOperationException("Programarea este deja anulată.");
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

        // daca nu exista nici o evaluare -> EVALUARE INITIALA (Specific Name)
        if (evaluareOpt.isEmpty()) {
            return serviciiClient.gasesteServiciuDupaNume(numeEvaluareInitiala);
        }

        Evaluare evaluare = evaluareOpt.get();

        // numaram toate sedintele finalizate de pacient de la data evalaurii
        long sedinteEfectuateTotal = programareRepository.countSedintePacientDupaData(
                pacientId,
                evaluare.getData());

        // daca a efectuat toate sedintele recomandate in evaluare -> S-a terminat pachetul -> REEVALUARE
        // daca nu -> Continuam cu SERVICIUL RECOMANDAT IN EVALUARE
        if (sedinteEfectuateTotal >= evaluare.getSedinteRecomandate()) {
            return serviciiClient.gasesteServiciuDupaNume(numeReevaluare);
        } else {
            return serviciiClient.getServiciuById(evaluare.getServiciuRecomandatId());
        }
    }

    // calculam sloturile orare libere
    @Transactional(readOnly = true)
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
        int durataMinute = serviciu.durataMinute();

        // obtinem lista de programari existente (ocupate)
        List<Programare> programariExistente = programareRepository.findByTerapeutIdAndDataAndStatus(
                terapeutId, data, StatusProgramare.PROGRAMATA);

        // generam sloturile libere
        List<LocalTime> sloturiLibere = new ArrayList<>();

        LocalTime cursor = orar.oraInceput();
        LocalTime limitaSfarsit = orar.oraSfarsit();

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
    @Transactional(readOnly = true)
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
                    numePacient = userDTO.nume() + " " + userDTO.prenume();
                    telefonPacient = userDTO.telefon();
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
                .orElseThrow(() -> new ResourceNotFoundException("Programarea nu există"));

        // Terapeutul trebuie sa fie cel din programare
        if (!programare.getTerapeutId().equals(terapeutId)) {
            throw new ForbiddenOperationException("Nu aveți dreptul să anulați această programare (nu vă aparține).");
        }

        // validare
        if (programare.getStatus() != StatusProgramare.PROGRAMATA) {
            throw new ForbiddenOperationException("Doar programările viitoare pot fi anulate.");
        }

        // modificam status si motiv anulare
        programare.setStatus(StatusProgramare.ANULATA);
        programare.setMotivAnulare(MotivAnulare.ANULAT_DE_TERAPEUT);

        programareRepository.save(programare);

        // notificare catre pacient
        notificarePublisher.programareAnulataDeTerapeut(programare);
    }

    // Cron Job pentru finalizarea automata a sedintelor
    // Ruleaza la fiecare 30 secunde (TEMPORAR - TESTARE)
    // gaseste programarile programate cu data si ora in trecut si le seteaza statusul finalizata
    @Scheduled(cron = "*/30 * * * * *")
    @Transactional
    public void finalizeazaProgramariExpirate() {
        LocalDate azi = LocalDate.now();
        LocalTime acum = LocalTime.now();

        List<Programare> expirate = programareRepository.findExpiredAppointments(azi, acum);

        if (!expirate.isEmpty()) {
            expirate.forEach(p -> p.setStatus(StatusProgramare.FINALIZATA));
            programareRepository.saveAll(expirate);
            log.info("Cron Job: S-au finalizat automat {} programări.", expirate.size());

            // pentru fiecare programare finalizata, trimitem notificari
            expirate.forEach(this::trimiteNotificariDupaFinalizare);
        }
    }

    // trimite REMINDER_JURNAL si verifica daca pacientul a terminat pachetul (REEVALUARE_RECOMANDATA)
    private void trimiteNotificariDupaFinalizare(Programare p) {
        // asiguram relatia pacient-terapeut la prima programare finalizata
        relatieService.asiguraRelatieActiva(p.getPacientId(), p.getTerapeutId(), p.getData());

        // reminder jurnal - pacientul trebuie sa completeze jurnalul
        notificarePublisher.reminderJurnal(p);

        // verificam daca pacientul a terminat toate sedintele din pachet
        // refolosim aceeasi logica din determinaServiciulCorect
        evaluareRepository.findFirstByPacientIdOrderByDataDesc(p.getPacientId())
                .ifPresent(evaluare -> {
                    if (evaluare.getSedinteRecomandate() != null) {
                        long sedinteEfectuate = programareRepository.countSedintePacientDupaData(
                                p.getPacientId(), evaluare.getData());
                        if (sedinteEfectuate == evaluare.getSedinteRecomandate()) {
                            notificarePublisher.reevaluareRecomandata(p);
                        }
                    }
                });
    }

    // sedinte finalizate fara jurnal
    @Transactional(readOnly = true)
    public List<ProgramareJurnalDTO> getProgramariFaraJurnal(Long pacientId) {
        List<Programare> programari = programareRepository.findByPacientIdAndStatusAndAreJurnalFalseOrderByDataDesc(
                pacientId, StatusProgramare.FINALIZATA);

        return programari.stream().map(p -> {
            String numeTerapeut = getNumeTerapeut(p.getTerapeutId());
            String terapeutKeycloakId = getKeycloakIdTerapeut(p.getTerapeutId());
            String numeLocatie = getNumeLocatie(p.getLocatieId());
            return programareMapper.toProgramareJurnalDTO(p, numeTerapeut, terapeutKeycloakId, numeLocatie);
        }).toList();
    }

    // seteaza areJurnal=true
    public void marcheazaProgramareCuJurnal(Long programareId) {
        Programare p = programareRepository.findById(programareId).orElseThrow();
        p.setAreJurnal(true);
        programareRepository.save(p);
    }

    // detaliile complete
    @Transactional(readOnly = true)
    public ProgramareJurnalDTO getDetaliiProgramare(Long programareId) {
        Programare p = programareRepository.findById(programareId)
                .orElseThrow(() -> new ResourceNotFoundException("Programarea nu a fost găsită"));

        String numeTerapeut = getNumeTerapeut(p.getTerapeutId());
        String terapeutKeycloakId = getKeycloakIdTerapeut(p.getTerapeutId());
        String numeLocatie = getNumeLocatie(p.getLocatieId());

        return programareMapper.toProgramareJurnalDTO(p, numeTerapeut, terapeutKeycloakId, numeLocatie);
    }

    // batch detalii - apelat de pacienti-service pentru a evita N+1 calls la jurnal/istoric
    @Transactional(readOnly = true)
    public List<ProgramareJurnalDTO> getDetaliiProgramareBatch(List<Long> programareIds) {
        if (programareIds == null || programareIds.isEmpty()) {
            return List.of();
        }
        // Încărcăm toate programările într-o singură interogare DB
        List<Programare> programari = programareRepository.findAllById(programareIds);

        return programari.stream().map(p -> {
            String numeTerapeut = getNumeTerapeut(p.getTerapeutId());
            String terapeutKeycloakId = getKeycloakIdTerapeut(p.getTerapeutId());
            String numeLocatie = getNumeLocatie(p.getLocatieId());
            return programareMapper.toProgramareJurnalDTO(p, numeTerapeut, terapeutKeycloakId, numeLocatie);
        }).toList();
    }

    // returneaza istoricul complet al programarilor unui pacient
    @Transactional(readOnly = true)
    public List<IstoricProgramareDTO> getIstoricPacient(Long pacientId) {
        List<Programare> programari = programareRepository.findAllByPacientIdOrderByDataDescOraInceputDesc(pacientId);

        // Fetch journal history
        List<JurnalIstoricDTO> jurnale = new ArrayList<>();
        try {
            jurnale = pacientiClient.getIstoricJurnal(pacientId);
        } catch (Exception e) {
            log.warn("Nu s-a putut prelua istoricul jurnalului pentru pacientul {}: {}", pacientId, e.getMessage());
        }

        // Map journals by programareId
        Map<Long, JurnalIstoricDTO> jurnalMap = jurnale.stream()
                .filter(j -> j.programareId() != null)
                .collect(Collectors.toMap(JurnalIstoricDTO::programareId, Function.identity(), (existing, replacement) -> existing));

        return programari.stream().map(p -> {
            String numeTerapeut = getNumeTerapeut(p.getTerapeutId());
            String numeLocatie = getNumeLocatie(p.getLocatieId());

            String serviciuRecomandat = null;
            Evaluare evaluare = null;
            if (Boolean.TRUE.equals(p.getAreEvaluare())) {
                evaluare = evaluareRepository.findByProgramareId(p.getId()).orElse(null);
                
                if (evaluare != null && evaluare.getServiciuRecomandatId() != null) {
                    try {
                        DetaliiServiciuDTO serviciuDTO = serviciiClient.getServiciuById(evaluare.getServiciuRecomandatId());
                        if (serviciuDTO != null) {
                            serviciuRecomandat = serviciuDTO.nume();
                        }
                    } catch (Exception e) {
                        log.warn("Nu s-a putut prelua numele serviciului recomandat pentru evaluarea {}: {}", evaluare.getId(), e.getMessage());
                        serviciuRecomandat = "Indisponibil";
                    }
                }
            }

            // Map Journal Details if exists
            DetaliiJurnalDTO detaliiJurnal = null;
            if (jurnalMap.containsKey(p.getId())) {
                JurnalIstoricDTO j = jurnalMap.get(p.getId());
                detaliiJurnal = new DetaliiJurnalDTO(
                        j.nivelDurere(),
                        j.dificultateExercitii(),
                        j.nivelOboseala(),
                        j.comentarii()
                );
            }

            return istoricProgramareMapper.toDTO(p, numeTerapeut, numeLocatie, evaluare, serviciuRecomandat, detaliiJurnal);
        }).toList();
    }

    // HELPER: obtine numele complet al terapeutului dupa terapeutId
    private String getNumeTerapeut(Long terapeutId) {
        try {
            String terapeutKeycloakId = terapeutiClient.getKeycloakIdByTerapeutId(terapeutId);
            if (terapeutKeycloakId != null) {
                UserDisplayCalendarDTO userTerapeut = userClient.getUserByKeycloakId(terapeutKeycloakId);
                if (userTerapeut != null) {
                    return userTerapeut.nume() + " " + userTerapeut.prenume();
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
                return locatie.nume();
            }
        } catch (Exception e) {
            log.error("Nu s-a putut prelua numele locatiei {}: {}", locatieId, e.getMessage());
        }
        return "Locație Necunoscută";
    }
    // Calculeaza situatia curenta a pacientului (Diagnostic + Progres Sedinte)
    @Transactional(readOnly = true)
    public SituatiePacientDTO getSituatiePacient(Long pacientId) {
        // 1. Cautam ultima evaluare
        Optional<Evaluare> evaluareOpt = evaluareRepository.findFirstByPacientIdOrderByDataDesc(pacientId);

        if (evaluareOpt.isEmpty()) {
            return evaluareMapper.toEmptySituatiePacientDTO();
        }

        Evaluare evaluare = evaluareOpt.get();

        // 2. Numaram sedintele efectuate de la data evaluarii
        long sedinteEfectuate = programareRepository.countSedintePacientDupaData(
                pacientId,
                evaluare.getData());

        return evaluareMapper.toSituatiePacientDTO(evaluare, sedinteEfectuate);
    }

    // anulare masiva cand pacientul schimba terapeutul preferat
    @Transactional
    public void anuleazaProgramariVechi(String pacientKeycloakId, String terapeutKeycloakId) {
        // resolve pacientKeycloakId -> pacientId (care este de fapt user id in db)
        UserDisplayCalendarDTO user = userClient.getUserByKeycloakId(pacientKeycloakId);
        Long pacientId = user.id();

        // resolve keycloakId -> terapeutId
        Map<String, Object> terapeut = terapeutiClient.getTerapeutByKeycloakId(terapeutKeycloakId);
        Long terapeutId = ((Number) terapeut.get("id")).longValue();

        // DEZACTIVAM Relația activă curentă cu fostul terapeut, forțându-l să ajungă în "arhivă"
        relatieService.dezactiveazaRelatiaActiva(pacientId);

        List<Programare> programari = programareRepository.findByPacientIdAndTerapeutIdAndStatusAndDataGreaterThanEqual(
                pacientId, terapeutId, StatusProgramare.PROGRAMATA, LocalDate.now(), LocalTime.now());

        if(!programari.isEmpty()) {
            programari.forEach(p -> {
                p.setStatus(StatusProgramare.ANULATA);
                // folosim ANULAT_DE_PACIENT deoarece pacientul a initiat schimbarea
                p.setMotivAnulare(MotivAnulare.ANULAT_DE_PACIENT);
            });
            programareRepository.saveAll(programari);
            log.info("S-au anulat automat {} programari viitoare intre pacientul {} si fostul terapeut {}", programari.size(), pacientId, terapeutId);

            // Optional: notificari
            programari.forEach(notificarePublisher::programareAnulataDePacient);
        }
    }

    // ADMIN: anulare programari la dezactivare cont

    // anuleaza toate programarile viitoare ale unui terapeut (keycloakId -> terapeutId -> cancel)
    @Transactional
    public AdminCancelResultDTO anuleazaProgramariAdminByTerapeut(String keycloakId) {
        // resolve keycloakId -> terapeutId
        Map<String, Object> terapeut = terapeutiClient.getTerapeutByKeycloakId(keycloakId);
        Long terapeutId = ((Number) terapeut.get("id")).longValue();

        int count = anuleazaProgramariViitoare(
                programareRepository.findByTerapeutIdAndStatusAndDataGreaterThanEqual(
                        terapeutId, StatusProgramare.PROGRAMATA, LocalDate.now(), LocalTime.now()));
        
        return new AdminCancelResultDTO(count, "Au fost anulate " + count + " programări viitoare pentru terapeut.");
    }

    // anuleaza toate programarile viitoare ale unui pacient (keycloakId -> pacientId -> cancel)
    @Transactional
    public AdminCancelResultDTO anuleazaProgramariAdminByPacient(String keycloakId) {
        // resolve keycloakId -> pacientId
        PacientKeycloakDTO pacient = pacientiClient.getByKeycloakId(keycloakId);
        Long pacientId = pacient.id();

        int count = anuleazaProgramariViitoare(
                programareRepository.findByPacientIdAndStatusAndDataGreaterThanEqual(
                        pacientId, StatusProgramare.PROGRAMATA, LocalDate.now(), LocalTime.now()));

        return new AdminCancelResultDTO(count, "Au fost anulate " + count + " programări viitoare pentru pacient.");
    }

    // helper: seteaza ANULATA + ADMINISTRATIV pe o lista de programari
    private int anuleazaProgramariViitoare(List<Programare> programari) {
        programari.forEach(p -> {
            p.setStatus(StatusProgramare.ANULATA);
            p.setMotivAnulare(MotivAnulare.ADMINISTRATIV);
        });
        programareRepository.saveAll(programari);
        log.info("Admin: anulate {} programări viitoare", programari.size());
        return programari.size();
    }

    // helper: terapeutId (terapeuti-service) → keycloakId
    private String getKeycloakIdTerapeut(Long terapeutId) {
        try {
            return terapeutiClient.getKeycloakIdByTerapeutId(terapeutId);
        } catch (Exception e) {
            log.warn("Nu s-a putut obtine keycloakId pentru terapeutId={}: {}", terapeutId, e.getMessage());
            return null;
        }
    }
}
