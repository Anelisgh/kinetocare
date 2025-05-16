package com.example.kinetocare.controller;

import com.example.kinetocare.dto.PacientHomeDTO;
import com.example.kinetocare.dto.ProgramareDTO;
import com.example.kinetocare.exception.ConflictException;
import com.example.kinetocare.service.PacientService;
import com.example.kinetocare.service.PlataService;
import com.example.kinetocare.service.ProgramareService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/pacient")
public class PacientController {
    private final PacientService pacientService;
    private final ProgramareService programareService;
    private final PlataService plataService;

    @GetMapping("/homepage")
    public String showHomePage(Model model, Principal principal) {
        log.info("User {} accessed patient homepage", principal.getName());
        String email = principal.getName();
        PacientHomeDTO pacientHomeDTO = pacientService.getPacientHomeDTO(email);
        model.addAttribute("pacientHome", pacientHomeDTO);
        model.addAttribute("formAction", pacientService.getFormAction(pacientHomeDTO));
        model.addAttribute("areProgramare", pacientService.hasProgramare(pacientHomeDTO));
        return "pacient/homepage";
    }

    @PostMapping("/programari/creaza")
    public String creazaProgramare(
            @Valid @ModelAttribute("pacientHome") PacientHomeDTO pacientHomeDTO,
            BindingResult result,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        log.info("Attempting to create appointment for {}", principal.getName());
        if (result.hasErrors()) {
            log.warn("Validation errors in create appointment: {}", result.getAllErrors());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.pacientHome", result);
            redirectAttributes.addFlashAttribute("pacientHome", pacientHomeDTO);
            return "redirect:/pacient/homepage";
        }

        try {
            ProgramareDTO programareDTO = pacientHomeDTO.getNouaProgramare();
            programareService.creazaProgramare(programareDTO, principal.getName());
            redirectAttributes.addFlashAttribute("success", "Programarea a fost creată cu succes!");
            log.debug("Appointment created successfully for {}", principal.getName());
        } catch (ConflictException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            log.error("Conflict in appointment creation: {}", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "A apărut o eroare la crearea programării");
            log.error("Unexpected error creating appointment", e);
        }

        return "redirect:/pacient/homepage";
    }

    @PostMapping("/programari/modifica")
    public String modificaProgramare(
            @Valid @ModelAttribute("nouaProgramare") ProgramareDTO programareDTO,
            BindingResult result,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        log.info("User {} trying to modify appointment {}", principal.getName(), programareDTO.getId());
        if (result.hasErrors()) {
            log.warn("Validation errors in modify appointment: {}", result.getAllErrors());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.nouaProgramare", result);
            redirectAttributes.addFlashAttribute("nouaProgramare", programareDTO);
            return "redirect:/pacient/homepage";
        }
        try {
            programareService.modificaProgramare(programareDTO, principal.getName());
            redirectAttributes.addFlashAttribute("success", "Programarea a fost modificată cu succes!");
            log.debug("Appointment {} modified successfully", programareDTO.getId());
        } catch (ConflictException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            log.error("Conflict modifying appointment: {}", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "A apărut o eroare la modificarea programării");
            log.error("Failed to modify appointment {}", programareDTO.getId(), e);
        }

        return "redirect:/pacient/homepage";
    }

    @PostMapping("/programari/sterge/{id}")
    public String stergeProgramare(@PathVariable Long id, Principal principal) {
        log.info("Deleting appointment {} for user {}", id, principal.getName());
        try {
            programareService.stergeProgramare(id, principal.getName());
            log.debug("Appointment {} deleted", id);
        } catch (Exception e) {
            log.error("Failed to delete appointment {}", id, e);
        }
        return "redirect:/pacient/homepage";
    }

    @GetMapping("/facturi")
    public String getFacturi(Model model, Principal principal,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             @RequestParam(defaultValue = "data,desc") String sort) {
        log.debug("Loading invoices for {}", principal.getName());
        if(!sort.matches("(data|suma),(asc|desc)")) {
            sort = "data,desc";
        }
        Sort.Direction direction = sort.endsWith("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortBy = sort.split(",")[0];

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Map<String, Object> dateFacturi = plataService.getPlatiPentruPacient(principal.getName(), pageable);
        model.addAllAttributes(dateFacturi);

        model.addAttribute("sort", sort);
        model.addAttribute("pageSize", size);

        return "pacient/facturi";
    }
}