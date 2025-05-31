package com.example.common.dto;

import com.example.common.enums.StarePlata;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStarePlataDTO {
    private StarePlata starePlata;
}
