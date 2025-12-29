package com.example.programari_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// pentru afisarea datelor pacientului in calendarul terapeutului
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDisplayCalendarDTO {
    private String nume;
    private String prenume;
    private String telefon;
}