package com.example.programari_service.service;

import com.example.programari_service.client.ServiciiClient;
import com.example.programari_service.client.TerapeutiClient;
import com.example.programari_service.dto.CreeazaProgramareRequest;
import com.example.programari_service.dto.DetaliiServiciuDTO;
import com.example.programari_service.dto.DisponibilitateDTO;
import com.example.programari_service.dto.UrmatoareaProgramareDTO;
import com.example.programari_service.entity.Evaluare;
import com.example.programari_service.entity.MotivAnulare;
import com.example.programari_service.entity.Programare;
import com.example.programari_service.entity.StatusProgramare;
import com.example.programari_service.mapper.ProgramareMapper;
import com.example.programari_service.repository.EvaluareRepository;
import com.example.programari_service.repository.ProgramareRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

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

    public Optional<UrmatoareaProgramareDTO> getUrmatoareaProgramare(Long pacientId) {
        // cerem pagina 0 cu 1 element (LIMIT 1)
        List<Programare> lista = programareRepository.gasesteUrmatoareaProgramare(
                pacientId,
                PageRequest.of(0, 1)
        );

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
                request.getPacientId()
        );
        // Calculam ora de sfarsit pe baza serviciului det automat
        LocalTime oraSfarsit = request.getOraInceput().plusMinutes(serviciuDeAplicat.getDurataMinute());

        // Verificam suprapuneri
        boolean eOcupat = programareRepository.existaSuprapunere(
                request.getTerapeutId(),
                request.getData(),
                request.getOraInceput(),
                oraSfarsit
        );

        if (eOcupat) {
            throw new RuntimeException("Intervalul orar este deja ocupat.");
        }

        Programare programareNoua = programareMapper.toEntity(
                request,
                serviciuDeAplicat,
                oraSfarsit,
                isPrimaIntalnire
        );

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
                evaluare.getData()
        );

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

        if (orar == null) return List.of();

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

        // parcurgem intervalul orar si verificam daca mai este loc de inca o sedinta (continuam atata timp cat finalul unui potential slot NU depaseste limita)
        while (!cursor.plusMinutes(durataMinute).isAfter(limitaSfarsit)) {
            LocalTime finalSlot = cursor.plusMinutes(durataMinute);

            // verificam daca slotul curent este liber avand in vedere programarile existente
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
}
