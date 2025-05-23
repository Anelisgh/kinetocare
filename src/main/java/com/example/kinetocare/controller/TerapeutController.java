package com.example.kinetocare.controller;

import com.example.kinetocare.domain.Pacient;
import com.example.kinetocare.domain.Status;
import com.example.kinetocare.domain.TipEvaluare;
import com.example.kinetocare.domain.TipServiciu;
import com.example.kinetocare.domain.security.User;
import com.example.kinetocare.dto.*;
import com.example.kinetocare.repository.PacientRepository;
import com.example.kinetocare.service.CalendarService;
import com.example.kinetocare.service.EvaluareService;
import com.example.kinetocare.service.EvolutieService;
import com.example.kinetocare.service.PacientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/terapeut")
@RequiredArgsConstructor
public class TerapeutController {
    private final EvaluareService evaluareService;
    private final PacientRepository pacientRepository;
    private final EvolutieService evolutieService;
    private final PacientService pacientService;
    private final CalendarService calendarService;

    @GetMapping("/homepage")
    public String showHomePage() {
        return "terapeut/homepage";
    }
//─── ⋆⋅☆⋅⋆ ────── ⋆⋅☆⋅⋆ ────── ⋆⋅☆⋅⋆ ──────
// ADAUGAREA EVALUARILOR
//─── ⋆⋅☆⋅⋆ ────── ⋆⋅☆⋅⋆ ────── ⋆⋅☆⋅⋆ ──────
    @GetMapping("/adaugare_evaluare")
    public String showForm(Model model) {
        List<Pacient> pacienti = pacientRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Pacient::getNume))
                .collect(Collectors.toList());
        model.addAttribute("evaluareDTO", new EvaluareDTO());
        model.addAttribute("pacienti", pacienti);
        model.addAttribute("tipuriEvaluare", TipEvaluare.values());
        model.addAttribute("servicii", getServiciiFiltrate());
        return "terapeut/adaugare_evaluare";
    }

    @PostMapping("/adaugare_evaluare")
    public String submitForm(@Valid @ModelAttribute EvaluareDTO evaluareDTO,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("pacienti", pacientRepository.findAll());
            model.addAttribute("tipuriEvaluare", TipEvaluare.values());
            model.addAttribute("servicii", getServiciiFiltrate());
            return "terapeut/adaugare_evaluare";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = ((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername();
        evaluareService.adaugaEvaluare(evaluareDTO, email);

        return "redirect:/terapeut/homepage";
    }

    private List<TipServiciu> getServiciiFiltrate() {
        return Arrays.stream(TipServiciu.values())
                .filter(t -> t != TipServiciu.EVALUARE && t != TipServiciu.REEVALUARE)
                .collect(Collectors.toList());
    }

//─── ⋆⋅☆⋅⋆ ────── ⋆⋅☆⋅⋆ ────── ⋆⋅☆⋅⋆ ──────
//ADAUGAREA EVOLUTIILOR
//─── ⋆⋅☆⋅⋆ ────── ⋆⋅☆⋅⋆ ────── ⋆⋅☆⋅⋆ ──────
    @GetMapping("/adaugare_evolutie")
    public String showPacientiEvolutie(Model model){
        List<Pacient> pacienti = pacientRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Pacient::getNume))
                .collect(Collectors.toList());
        model.addAttribute("pacienti", pacienti);
        model.addAttribute("evolutieDTO", new EvolutieDTO());
        return "terapeut/adaugare_evolutie";
    }

    @PostMapping("adaugare_evolutie")
    public String submitForm(@Valid @ModelAttribute EvolutieDTO evolutieDTO,
                                 BindingResult result,
                                 Model model) {
        if (result.hasErrors()) {
            model.addAttribute("pacienti", pacientRepository.findAll());
            return "terapeut/adaugare_evolutie";
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = ((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername();
        evolutieService.adaugaEvolutie(evolutieDTO, email);
        return "redirect:/terapeut/homepage";
    }

//─── ⋆⋅☆⋅⋆ ────── ⋆⋅☆⋅⋆ ────── ⋆⋅☆⋅⋆ ──────
// LISTA PACIENTI
//─── ⋆⋅☆⋅⋆ ────── ⋆⋅☆⋅⋆ ────── ⋆⋅☆⋅⋆ ──────
    @GetMapping("/pacienti")
    public String showPacienti(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = ((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername();

        List<PacientDTO> pacienti = pacientService.getPacientiForTerapeut(email);
        model.addAttribute("pacienti", pacienti);
        return "terapeut/pacienti";
    }

//─── ⋆⋅☆⋅⋆ ────── ⋆⋅☆⋅⋆ ────── ⋆⋅☆⋅⋆ ──────
// DETALII PACIENTI
//─── ⋆⋅☆⋅⋆ ────── ⋆⋅☆⋅⋆ ────── ⋆⋅☆⋅⋆ ──────
    @GetMapping("/pacienti/{id}")
    public String showDetaliiPacient(@PathVariable Long id, Model model) {
        PacientDetaliiDTO detaliiDTO = pacientService.getDetaliiPacient(id);
        model.addAttribute("pacient", detaliiDTO);
        return "terapeut/detalii_pacient";
    }

//─── ⋆⋅☆⋅⋆ ────── ⋆⋅☆⋅⋆ ────── ⋆⋅☆⋅⋆ ──────
// CALENDAR HOMEPAGE
//─── ⋆⋅☆⋅⋆ ────── ⋆⋅☆⋅⋆ ────── ⋆⋅☆⋅⋆ ──────
    @GetMapping("/calendar")
    public String showCalendar(Model model) {
        model.addAttribute("programari", getProgramariForCurrentTerapeut());
        return "terapeut/homepage";
    }

    @PostMapping("/update-programare")
    @ResponseBody
    public ResponseEntity<?> updateProgramare(
            @RequestParam Long programareId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime newTime,
            @RequestParam Status newStatus) {

        calendarService.updateProgramare(programareId, newDate, newTime, newStatus);
        return ResponseEntity.ok().build();
    }

    private List<ProgramareTerapeutDTO> getProgramariForCurrentTerapeut() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return calendarService.getProgramariForTerapeut(email);
    }

    @GetMapping("/programari")
    @ResponseBody
    public List<Map<String, Object>> getProgramariForCalendar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        List<ProgramareTerapeutDTO> programari = calendarService.getProgramariForCalendar(
                email, start.toLocalDate(), end.toLocalDate());

        return programari.stream()
                .map(p -> {
                    Map<String, Object> event = new HashMap<>();
                    event.put("id", p.getProgramareId());
                    event.put("title", p.getNumePacient() + " - " + p.getTipServiciu().getDisplayName());
                    event.put("start", LocalDateTime.of(p.getData(), p.getOraStart()));
                    event.put("end", LocalDateTime.of(p.getData(), p.getOraEnd()));
                    event.put("status", p.getStatus().name());
                    return event;
                })
                .collect(Collectors.toList());
    }
}
