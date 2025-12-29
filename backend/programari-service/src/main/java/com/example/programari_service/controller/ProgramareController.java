package com.example.programari_service.controller;

import com.example.programari_service.dto.*;
import com.example.programari_service.entity.Programare;
import com.example.programari_service.service.ProgramareService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/programari")
@RequiredArgsConstructor
public class ProgramareController {

    private final ProgramareService programareService;

    @PostMapping
    public ResponseEntity<Programare> creeazaProgramare(@RequestBody CreeazaProgramareRequest request) {
        Programare noua = programareService.creeazaProgramare(request);
        return ResponseEntity.ok(noua);
    }

    @GetMapping("/pacient/{id}/next")
    public ResponseEntity<UrmatoareaProgramareDTO> getUrmatoareaProgramare(@PathVariable("id") Long pacientId) {
        return programareService.getUrmatoareaProgramare(pacientId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> anuleazaProgramare(@PathVariable Long id, @RequestParam Long pacientId) {
        programareService.anuleazaProgramare(id, pacientId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/neprezentare")
    public ResponseEntity<Void> marcheazaNeprezentare(
            @PathVariable Long id,
            @RequestParam(required = false) Long terapeutId, // fals, pentru ca poate fi trimis de admin
            @RequestParam(defaultValue = "false") boolean isAdmin) { // daca e trimis de admin

        programareService.marcheazaNeprezentare(id, terapeutId, isAdmin);
        return ResponseEntity.noContent().build();
    }

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

    @GetMapping("/serviciu-recomandat")
    public ResponseEntity<DetaliiServiciuDTO> getServiciuRecomandat(@RequestParam Long pacientId) {
        DetaliiServiciuDTO serviciu = programareService.determinaServiciulCorect(pacientId);
        return ResponseEntity.ok(serviciu);
    }

    // calendarul pacientului
    @GetMapping("/calendar")
    public ResponseEntity<List<CalendarProgramareDTO>> getCalendar(
            @RequestParam Long terapeutId,
            @RequestParam String start, // Format: yyyy-MM-dd
            @RequestParam String end, // Format: yyyy-MM-dd
            @RequestParam(required = false) Long locatieId) {

        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);

        List<CalendarProgramareDTO> calendarData = programareService.getCalendarAppointments(terapeutId, startDate,
                endDate, locatieId);

        return ResponseEntity.ok(calendarData);
    }

    @PatchMapping("/{id}/cancel-terapeut")
    public ResponseEntity<Void> anuleazaProgramareTerapeut(
            @PathVariable Long id,
            @RequestParam Long terapeutId) {

        programareService.anuleazaProgramareTerapeut(id, terapeutId);
        return ResponseEntity.noContent().build();
    }

    // JURNAL PACIENT
    @GetMapping("/pacient/{id}/necompletate")
    public ResponseEntity<List<ProgramareJurnalDTO>> getProgramariFaraJurnal(@PathVariable Long id) {
        return ResponseEntity.ok(programareService.getProgramariFaraJurnal(id));
    }

    @PostMapping("/{id}/mark-jurnal")
    public ResponseEntity<Void> marcheazaJurnal(@PathVariable Long id) {
        programareService.marcheazaProgramareCuJurnal(id);
        return ResponseEntity.ok().build();
    }

    // Endpoint pentru a obține detalii programare (folosit de pacienti-service
    // pentru jurnal)
    @GetMapping("/{id}/detalii")
    public ResponseEntity<ProgramareJurnalDTO> getDetaliiProgramare(@PathVariable Long id) {
        return ResponseEntity.ok(programareService.getDetaliiProgramare(id));
    }
}
