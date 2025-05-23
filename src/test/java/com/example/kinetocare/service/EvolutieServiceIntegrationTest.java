package com.example.kinetocare.service;

import com.example.kinetocare.domain.Evolutie;
import com.example.kinetocare.dto.EvolutieDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
class EvolutieServiceIntegrationTest extends AbstractIntegrationTest{
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
    void adaugaEvolutie_Success() {
        // Given
        EvolutieDTO dto = TestUtils.createValidEvolutieDTO(pacient.getId());

        // When
        evolutieService.adaugaEvolutie(dto, terapeut.getUser().getEmail());

        // Then
        List<Evolutie> evolutii = evolutieRepository.findAll();
        assertThat(evolutii).hasSize(1);

        Evolutie evolutie = evolutii.get(0);
        assertThat(evolutie.getData()).isEqualTo(dto.getDataEvolutie());
        assertThat(evolutie.getObservatii()).isEqualTo(dto.getObservatii());
        assertThat(evolutie.getPacient()).isEqualTo(pacient);
        assertThat(evolutie.getTerapeut()).isEqualTo(terapeut);
    }

    @Test
    void adaugaEvolutie_TerapeutNotFound_ShouldThrow() {
        // Given
        EvolutieDTO dto = TestUtils.createValidEvolutieDTO(pacient.getId());

        // When & Then
        assertThatThrownBy(() -> evolutieService.adaugaEvolutie(dto, "invalid@email.com"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Terapeut nu există");
    }

    @Test
    void adaugaEvolutie_PacientInvalid_ShouldThrow() {
        // Given
        EvolutieDTO dto = TestUtils.createValidEvolutieDTO(999L);

        // When & Then
        assertThatThrownBy(() -> evolutieService.adaugaEvolutie(dto, terapeut.getUser().getEmail()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Pacient nu există");
    }

    @Test
    void adaugaEvolutie_DataViitoare_ShouldFailValidation() {
        // Given
        EvolutieDTO dto = TestUtils.createValidEvolutieDTO(pacient.getId());
        dto.setDataEvolutie(LocalDate.now().plusDays(1));

        // When & Then
        assertThatThrownBy(() -> evolutieService.adaugaEvolutie(dto, terapeut.getUser().getEmail()))
                .isInstanceOf(ConstraintViolationException.class);
    }
}
