package com.example.kinetocare.service;

import com.example.kinetocare.domain.*;
import com.example.kinetocare.dto.EvaluareDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
class EvaluareServiceIntegrationTest extends AbstractIntegrationTest{
    @BeforeEach
    void setUp() {
        TestUtils.clearDatabase(
                programareRepository,
                plataRepository,
                userRepository,
                authorityRepository,
                serviciuRepository,
                diagnosticRepository,
                evaluareRepository,
                pacientRepository,
                terapeutRepository,
                evolutieRepository
        );
        serviciu = Serviciu.builder()
                .tipServiciu(TipServiciu.MASAJ)
                .pret(BigDecimal.valueOf(100))
                .durataMinute(60)
                .build();
        serviciu = serviciuRepository.save(serviciu);

        // Folosim TestUtils
        terapeut = TestUtils.createAndSaveTerapeut(
                TestUtils.createTerapeutRegistrationDTO(),
                userRepository,
                authorityRepository,
                terapeutRepository
        );
        pacient = TestUtils.createAndSavePacient(
                TestUtils.createPacientRegistrationDTO(),
                terapeut,
                userRepository,
                authorityRepository,
                pacientRepository
        );
    }

    @Test
    void adaugaEvaluare_Success() {
        EvaluareDTO dto = TestUtils.createValidEvaluareDTO(pacient.getId());
        evaluareService.adaugaEvaluare(dto, terapeut.getUser().getEmail());

        List<Diagnostic> diagnostice = diagnosticRepository.findAll();
        List<Evaluare> evaluari = evaluareRepository.findAll();
        Pacient updatedPacient = pacientRepository.findById(pacient.getId()).orElseThrow();

        assertThat(diagnostice).hasSize(1);
        assertThat(evaluari).hasSize(1);
        assertThat(updatedPacient.getTerapeut()).isEqualTo(terapeut);

        Diagnostic diagnostic = diagnostice.get(0);
        Evaluare evaluare = evaluari.get(0);

        assertThat(diagnostic.getNume()).isEqualTo(dto.getNumeDiagnostic());
        assertThat(diagnostic.getSedinteRecomandate()).isEqualTo(dto.getSedinteRecomandate());
        assertThat(diagnostic.getServiciu()).isEqualTo(serviciu);

        assertThat(evaluare.getTipEvaluare()).isEqualTo(dto.getTipEvaluare());
        assertThat(evaluare.getData()).isEqualTo(dto.getDataEvaluare());
        assertThat(evaluare.getPacient()).isEqualTo(pacient);
        assertThat(evaluare.getTerapeut()).isEqualTo(terapeut);
    }

    @Test
    void adaugaEvaluare_TerapeutNotFound_ShouldThrow() {
        EvaluareDTO dto = TestUtils.createValidEvaluareDTO(pacient.getId());
        assertThatThrownBy(() -> evaluareService.adaugaEvaluare(dto, "email.inexistent@test.com"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Terapeutul nu există");
    }

    @Test
    void adaugaEvaluare_PacientInvalid_ShouldThrow() {
        EvaluareDTO dto = TestUtils.createValidEvaluareDTO(999L);
        assertThatThrownBy(() -> evaluareService.adaugaEvaluare(dto, terapeut.getUser().getEmail()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Pacientul nu există");
    }

    @Test
    void adaugaEvaluare_DataViitoare_ShouldFailValidation() {
        EvaluareDTO dto = TestUtils.createValidEvaluareDTO(pacient.getId());
        dto.setDataEvaluare(LocalDate.now().plusDays(1));
        assertThatThrownBy(() -> evaluareService.adaugaEvaluare(dto, terapeut.getUser().getEmail()))
                .isInstanceOf(ConstraintViolationException.class);
    }
}
