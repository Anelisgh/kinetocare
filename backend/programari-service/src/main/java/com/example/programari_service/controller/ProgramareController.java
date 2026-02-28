package com.example.programari_service.controller;

import com.example.programari_service.dto.*;
import com.example.programari_service.entity.Programare;
import com.example.programari_service.mapper.ProgramareMapper;
import com.example.programari_service.service.ProgramareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
    @GetMapping("/pacient/by-keycloak/{pacientKeycloakId}/next")
    public ResponseEntity<UrmatoareaProgramareDTO> getUrmatoareaProgramare(@PathVariable String pacientKeycloakId) {
        return programareService.getUrmatoareaProgramare(pacientKeycloakId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    // anulare de pacient (seteaza status ANULAT + motiv ANULAT_DE_PACIENT)
    // api-gateway -> anuleazaProgramare (ProgramariController)
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> anuleazaProgramare(@PathVariable Long id, @RequestParam String pacientKeycloakId) {
        programareService.anuleazaProgramare(id, pacientKeycloakId);
        return ResponseEntity.noContent().build();
    }

    // marcheaza neprezentare (seteaza status ANULATA + motiv NEPREZENTARE)
    // api-gateway -> marcheazaNeprezentare (ProgramariController)
    @PatchMapping("/{id}/neprezentare")
    public ResponseEntity<Void> marcheazaNeprezentare(
            @PathVariable Long id,
            @RequestParam(required = false) String terapeutKeycloakId, // fals, pentru ca poate fi trimis de admin
            @RequestParam(defaultValue = "false") boolean isAdmin) { // daca e trimis de admin

        programareService.marcheazaNeprezentare(id, terapeutKeycloakId, isAdmin);
        return ResponseEntity.noContent().build();
    }

    // calculeaza sloturile orare libere pentru o zi specifica
    @GetMapping("/disponibilitate")
    public ResponseEntity<List<LocalTime>> getDisponibilitate(
            @RequestParam(required = false) String terapeutKeycloakId,
            @RequestParam Long locatieId,
            @RequestParam String data, // yyyy-mm-dd
            @RequestParam Long serviciuId,
            @AuthenticationPrincipal Jwt jwt) {

        String tId = (terapeutKeycloakId != null) ? terapeutKeycloakId : jwt.getSubject();
        LocalDate date = LocalDate.parse(data);
        List<LocalTime> sloturi = programareService.getSloturiDisponibile(tId, locatieId, date, serviciuId);

        return ResponseEntity.ok(sloturi);
    }

    // determina automat serviciul corect pentru urmatoarea programare
    @GetMapping("/serviciu-recomandat")
    public ResponseEntity<DetaliiServiciuDTO> getServiciuRecomandat(
            @RequestParam(required = false) String pacientKeycloakId,
            @AuthenticationPrincipal Jwt jwt) {
        String pId = (pacientKeycloakId != null) ? pacientKeycloakId : jwt.getSubject();
        DetaliiServiciuDTO serviciu = programareService.determinaServiciulCorect(pId);
        return ResponseEntity.ok(serviciu);
    }

    // returneaza programarile intr-un interval
    @GetMapping("/calendar")
    public ResponseEntity<List<CalendarProgramareDTO>> getCalendar(
            @RequestParam(required = false) String terapeutKeycloakId,
            @RequestParam String start, // Format: yyyy-MM-dd
            @RequestParam String end, // Format: yyyy-MM-dd
            @RequestParam(required = false) Long locatieId,
            @AuthenticationPrincipal Jwt jwt) {

        String tId = (terapeutKeycloakId != null) ? terapeutKeycloakId : jwt.getSubject();
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);

        List<CalendarProgramareDTO> calendarData = programareService.getCalendarAppointments(tId, startDate, endDate, locatieId);

        return ResponseEntity.ok(calendarData);
    }

    // anulare de terapeut
    @PatchMapping("/{id}/cancel-terapeut")
    public ResponseEntity<Void> anuleazaProgramareTerapeut(
            @PathVariable Long id,
            @RequestParam(required = false) String terapeutKeycloakId,
            @AuthenticationPrincipal Jwt jwt) {

        String tId = (terapeutKeycloakId != null) ? terapeutKeycloakId : jwt.getSubject();
        programareService.anuleazaProgramareTerapeut(id, tId);
        return ResponseEntity.noContent().build();
    }

    // returneaza programarile finalizate pentru care pacientul logat nu a completat jurnalul
    @GetMapping("/necompletate")
    public ResponseEntity<List<ProgramareJurnalDTO>> getProgramariFaraJurnal(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();
        return ResponseEntity.ok(programareService.getProgramariFaraJurnal(keycloakId));
    }

    // returneaza programarile finalizate pentru care un pacient specific nu a completat jurnalul
    @GetMapping("/pacient/by-keycloak/{keycloakId}/necompletate")
    public ResponseEntity<List<ProgramareJurnalDTO>> getProgramariFaraJurnal(@PathVariable String keycloakId) {
        return ResponseEntity.ok(programareService.getProgramariFaraJurnal(keycloakId));
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

    // batch detalii programare - apelat de pacienti-service (JurnalService.getIstoric) pentru a evita N+1 calls
    // pacienti-service -> getProgramariBatch (ProgramariClient)
    @PostMapping("/batch-detalii")
    public ResponseEntity<List<ProgramareJurnalDTO>> getDetaliiProgramareBatch(@RequestBody List<Long> programareIds) {
        return ResponseEntity.ok(programareService.getDetaliiProgramareBatch(programareIds));
    }

    // returneaza istoricul complet al programarilor unui pacient
    @GetMapping("/pacient/by-keycloak/{keycloakId}/istoric")
    public ResponseEntity<List<IstoricProgramareDTO>> getIstoricPacient(@PathVariable String keycloakId) {
        return ResponseEntity.ok(programareService.getIstoricPacient(keycloakId));
    }
    // returneaza situatia pacientului (diagnostic + progres sedinte)
    // api-gateway -> getHomepage (HomepageController)
    @GetMapping("/pacient/by-keycloak/{keycloakId}/situatie")
    public ResponseEntity<SituatiePacientDTO> getSituatiePacient(@PathVariable String keycloakId) {
        return ResponseEntity.ok(programareService.getSituatiePacient(keycloakId));
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
