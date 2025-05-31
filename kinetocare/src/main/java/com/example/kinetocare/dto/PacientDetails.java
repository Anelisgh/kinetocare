package com.example.kinetocare.dto;

import com.example.kinetocare.domain.Gen;
import com.example.kinetocare.domain.TipSport;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

// 📢❗🚨 PENTRU REGISTER 📢❗🚨
@Data
public class PacientDetails {
    @NotBlank
    private String nume;
    @NotBlank
    private String telefon;
    @NotNull
    private Gen gen;
    @Pattern(regexp = "\\d{13}")
    private String cnp;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past
    private LocalDate dataNastere;
    @NotNull
    private TipSport tipSport;
    private String detaliiSport;
}