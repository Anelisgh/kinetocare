package com.example.kinetocare.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class AuthController {

    @GetMapping({"/", "/login"})
    public String showLoginForm(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                Model model) {
        if (error != null) {
            model.addAttribute("error", true);
        }
        if (logout != null) {
            model.addAttribute("logout", true);
        }
        return "login";
    }

    @GetMapping("/homepage")
    public String redirectAfterLogin(Authentication authentication) {
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        log.info("User with role {} logged in", role);
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PACIENT"))) {
            return "redirect:/pacient/homepage";
        } else if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TERAPEUT"))) {
            return "redirect:/terapeut/homepage";
        }
        return "redirect:/";
    }
}
