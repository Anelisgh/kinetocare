package com.example.kinetocare.dto;

import com.example.kinetocare.domain.TipServiciu;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiciuDTO {
    private TipServiciu tipServiciu;
}
