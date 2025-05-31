package com.example.kinetocare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

// 📢❗🚨 PENTRU REGISTER 📢❗🚨
@Data
public class TerapeutDetails {
    @NotBlank
    private String nume;
    @NotBlank
    private String telefon;
    @Pattern(regexp = "\\d{13}")
    private String cnp;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past
    private LocalDate dataNastere;
}
