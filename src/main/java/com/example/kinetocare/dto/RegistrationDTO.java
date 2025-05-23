package com.example.kinetocare.dto;

import com.example.kinetocare.domain.Gen;
import com.example.kinetocare.domain.TipSport;
import com.example.kinetocare.domain.security.RoleType;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegistrationDTO {
    @NotBlank @Email
    private String email;
    @NotBlank @Size(min = 6)
    private String password;
    @NotNull
    private RoleType roleType;

    @Valid
    @Nullable
    private PacientDetails pacientDetails;
    @Valid
    @Nullable
    private TerapeutDetails terapeutDetails;

    @AssertTrue(message = "Detaliile pacientului sunt obligatorii")
    public boolean isPacientDetailsValid() {
        if (roleType == RoleType.ROLE_PACIENT) {
            return pacientDetails != null
                    && !StringUtils.isEmpty(pacientDetails.getNume())
                    && !StringUtils.isEmpty(pacientDetails.getTelefon())
                    && !StringUtils.isEmpty(pacientDetails.getCnp())
                    && pacientDetails.getGen() != null
                    && pacientDetails.getDataNastere() != null
                    && pacientDetails.getTipSport() != null;
        }
        return true;
    }

    @AssertTrue(message = "Detaliile terapeutului sunt obligatorii")
    public boolean isTerapeutDetailsValid() {
        if (roleType == RoleType.ROLE_TERAPEUT) {
            return terapeutDetails != null
                    && !StringUtils.isEmpty(terapeutDetails.getNume())
                    && !StringUtils.isEmpty(terapeutDetails.getTelefon())
                    && !StringUtils.isEmpty(terapeutDetails.getCnp())
                    && terapeutDetails.getDataNastere() != null;
        }
        return true;
    }
}