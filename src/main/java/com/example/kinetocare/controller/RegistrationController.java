package com.example.kinetocare.controller;

import com.example.kinetocare.domain.TipSport;
import com.example.kinetocare.domain.security.RoleType;
import com.example.kinetocare.dto.PacientDetails;
import com.example.kinetocare.dto.RegistrationDTO;
import com.example.kinetocare.dto.TerapeutDetails;
import com.example.kinetocare.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@Controller
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @ModelAttribute("sportTranslations")
    public Map<String, String> sportTranslations() {
        return Map.of(
                "NU_FAC_SPORT", "Nu fac sport",
                "DE_PERFORMANTA", "Sport de performanță",
                "HOBBY", "Ca hobby"
        );
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        RegistrationDTO registrationDTO = new RegistrationDTO();
        registrationDTO.setPacientDetails(new PacientDetails());
        registrationDTO.setTerapeutDetails(new TerapeutDetails());
        model.addAttribute("registrationDTO", registrationDTO);
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("registrationDTO") RegistrationDTO registrationDTO,
            BindingResult result,
            Model model
    ) {
        if (registrationDTO.getRoleType() == RoleType.ROLE_PACIENT) {
            if (registrationDTO.getPacientDetails() == null) {
                registrationDTO.setPacientDetails(new PacientDetails());
            }
            registrationDTO.setTerapeutDetails(null);
        } else if (registrationDTO.getRoleType() == RoleType.ROLE_TERAPEUT) {
            if (registrationDTO.getTerapeutDetails() == null) {
                registrationDTO.setTerapeutDetails(new TerapeutDetails());
            }
            registrationDTO.setPacientDetails(null);
        }

        if (result.hasErrors()) {
            model.addAttribute("sportTranslations", sportTranslations());
            return "register";
        }

        registrationService.registerUser(registrationDTO);
        return "redirect:/login";
    }
}
