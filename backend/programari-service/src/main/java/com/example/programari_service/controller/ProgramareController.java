package com.example.programari_service.controller;

import com.example.programari_service.dto.*;
import com.example.programari_service.entity.Programare;
import com.example.programari_service.mapper.ProgramareMapper;
import com.example.programari_service.service.ProgramareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/programari")
@RequiredArgsConstructor
@Slf4j
public class ProgramareController {

    private final ProgramareService programareService;
    private final ProgramareMapper programareMapper;

    // creaza o programare noua (det automat serviciul corect, calc ora de sf, verifica suprapuneri)
    // api-gateway -> creeazaProgramare (ProgramariController)
    @PostMapping
    public ResponseEntity<ProgramareResponseDTO> creeazaProgramare(@RequestBody CreeazaProgramareRequest request) {
        Programare noua = programareService.creeazaProgramare(request);
        return ResponseEntity.ok(programareMapper.toResponseDTO(noua));
    }

    // returneaza urmatoarea programare activa a pacientului (cea mai apropiata)
    // api-gateway -> getHomepage (HomepageController)
    @GetMapping("/pacient/{id}/next")
    public ResponseEntity<UrmatoareaProgramareDTO> getUrmatoareaProgramare(@PathVariable("id") Long pacientId) {
        return programareService.getUrmatoareaProgramare(pacientId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    // anulare de pacient (seteaza status ANULAT + motiv ANULAT_DE_PACIENT)
    // api-gateway -> anuleazaProgramare (ProgramariController)
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> anuleazaProgramare(@PathVariable Long id, @RequestParam Long pacientId) {
        programareService.anuleazaProgramare(id, pacientId);
        return ResponseEntity.noContent().build();
    }

    // marcheaza neprezentare (seteaza status ANULATA + motiv NEPREZENTARE)
    // api-gateway -> marcheazaNeprezentare (ProgramariController)
    @PatchMapping("/{id}/neprezentare")
    public ResponseEntity<Void> marcheazaNeprezentare(
            @PathVariable Long id,
            @RequestParam(required = false) Long terapeutId, // fals, pentru ca poate fi trimis de admin
            @RequestParam(defaultValue = "false") boolean isAdmin) { // daca e trimis de admin

        programareService.marcheazaNeprezentare(id, terapeutId, isAdmin);
        return ResponseEntity.noContent().build();
    }

    // calculeaza sloturile orare libere pentru o zi specifica (verifica concedii, orar, programari existente si genereaza intervalele disponibile)
    // api-gateway -> getSloturiDisponibile (ProgramariController)
    @GetMapping("/disponibilitate")
    public ResponseEntity<List<LocalTime>> getDisponibilitate(
            @RequestParam Long terapeutId,
            @RequestParam Long locatieId,
            @RequestParam String data, // yyyy-mm-dd
            @RequestParam Long serviciuId) {

        LocalDate date = LocalDate.parse(data);
        List<LocalTime> sloturi = programareService.getSloturiDisponibile(terapeutId, locatieId, date, serviciuId);

        return ResponseEntity.ok(sloturi);
    }

    // determina autoamt serviciul corect pentru urmatoarea programare
    // api-gateway -> getServiciuRecomandat (ProgramariController)
    @GetMapping("/serviciu-recomandat")
    public ResponseEntity<DetaliiServiciuDTO> getServiciuRecomandat(@RequestParam Long pacientId) {
        DetaliiServiciuDTO serviciu = programareService.determinaServiciulCorect(pacientId);
        return ResponseEntity.ok(serviciu);
    }

    // returneaza programarile intr-un interval (exclude anularile, dar pastreaza neprezentarile)
    // api-gateway -> getCalendarTerapeut (ProgramariController)
    @GetMapping("/calendar")
    public ResponseEntity<List<CalendarProgramareDTO>> getCalendar(
            @RequestParam Long terapeutId,
            @RequestParam String start, // Format: yyyy-MM-dd
            @RequestParam String end, // Format: yyyy-MM-dd
            @RequestParam(required = false) Long locatieId) {

        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);

        List<CalendarProgramareDTO> calendarData = programareService.getCalendarAppointments(terapeutId, startDate, endDate, locatieId);

        return ResponseEntity.ok(calendarData);
    }

    // anulare de terapeut (seteaza status ANULAT + motiv ANULAT_DE_TERAPEUT)
    // api-gateway -> anuleazaProgramareTerapeut (ProgramariController)
    @PatchMapping("/{id}/cancel-terapeut")
    public ResponseEntity<Void> anuleazaProgramareTerapeut(
            @PathVariable Long id,
            @RequestParam Long terapeutId) {

        programareService.anuleazaProgramareTerapeut(id, terapeutId);
        return ResponseEntity.noContent().build();
    }

    // returneaza programarile finalizate pentru care pacientul nu a completat jurnalul
    // api-gateway -> getProgramariFaraJurnal (ProgramariController)
    @GetMapping("/pacient/{id}/necompletate")
    public ResponseEntity<List<ProgramareJurnalDTO>> getProgramariFaraJurnal(@PathVariable Long id) {
        return ResponseEntity.ok(programareService.getProgramariFaraJurnal(id));
    }

    // marcheaza programarea ca avand jurnal completat
    // pacienti-service -> marcheazaJurnal (ProgramariClient)
    @PostMapping("/{id}/mark-jurnal")
    public ResponseEntity<Void> marcheazaJurnal(@PathVariable Long id) {
        programareService.marcheazaProgramareCuJurnal(id);
        return ResponseEntity.ok().build();
    }

    // returneaza detalii programare complete
    // pacienti-service -> getDetaliiProgramare (ProgramariClient)
    @GetMapping("/{id}/detalii")
    public ResponseEntity<ProgramareJurnalDTO> getDetaliiProgramare(@PathVariable Long id) {
        return ResponseEntity.ok(programareService.getDetaliiProgramare(id));
    }

    // returneaza istoricul complet al programarilor unui pacient
    @GetMapping("/pacient/{id}/istoric")
    public ResponseEntity<List<IstoricProgramareDTO>> getIstoricPacient(@PathVariable Long id) {
        return ResponseEntity.ok(programareService.getIstoricPacient(id));
    }
    // returneaza situatia pacientului (diagnostic + progres sedinte)
    // api-gateway -> getHomepage (HomepageController)
    @GetMapping("/pacient/{id}/situatie")
    public ResponseEntity<SituatiePacientDTO> getSituatiePacient(@PathVariable Long id) {
        return ResponseEntity.ok(programareService.getSituatiePacient(id));
    }

    // Anulare programari vechi cand pacientul schimba terapeutul
    // pacienti-service -> anuleazaProgramariCuTerapeut (ProgramariClient)
    @DeleteMapping("/cancel-upcoming/pacient-keycloak/{pacientKeycloakId}/terapeut-keycloak/{terapeutKeycloakId}")
    public ResponseEntity<Void> cancelUpcomingAssignments(
            @PathVariable String pacientKeycloakId,
            @PathVariable String terapeutKeycloakId) {
        programareService.anuleazaProgramariVechi(pacientKeycloakId, terapeutKeycloakId);
        return ResponseEntity.noContent().build();
    }

    // ADMIN: anulare programari la dezactivare cont

    // anuleaza toate programarile viitoare ale unui terapeut
    // folosit in: user-service (toggleUserActive) — endpoint intern
    @PatchMapping("/admin/cancel-by-terapeut")
    public ResponseEntity<AdminCancelResultDTO> cancelByTerapeut(@RequestParam String keycloakId) {
        AdminCancelResultDTO result = programareService.anuleazaProgramariAdminByTerapeut(keycloakId);
        log.info("Admin: {}", result.message());
        return ResponseEntity.ok(result);
    }

    // anuleaza toate programarile viitoare ale unui pacient
    // folosit in: user-service (toggleUserActive) — endpoint intern
    @PatchMapping("/admin/cancel-by-pacient")
    public ResponseEntity<AdminCancelResultDTO> cancelByPacient(@RequestParam String keycloakId) {
        AdminCancelResultDTO result = programareService.anuleazaProgramariAdminByPacient(keycloakId);
        log.info("Admin: {}", result.message());
        return ResponseEntity.ok(result);
    }
}
