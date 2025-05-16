package com.example.kinetocare;

import com.example.kinetocare.domain.Evaluare;
import com.example.kinetocare.domain.Evolutie;
import com.example.kinetocare.domain.Pacient;
import com.example.kinetocare.domain.TipEvaluare;
import com.example.kinetocare.dto.PacientDTO;
import com.example.kinetocare.dto.PacientDetaliiDTO;
import com.example.kinetocare.dto.PacientHomeDTO;
import com.example.kinetocare.dto.ProgramareDTO;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
class PacientServiceIntegrationTest extends AbstractIntegrationTest{
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
    void getPacientiPentruTerapeut_shouldReturnOrderedPacienti() {
        Pacient p1 = new Pacient();
        p1.setNume("Z");
        p1.setTerapeut(terapeut);
        pacientRepository.save(p1);

        Pacient p2 = new Pacient();
        p2.setNume("A");
        p2.setTerapeut(terapeut);
        pacientRepository.save(p2);

        List<PacientDTO> result = pacientService.getPacientiPentruTerapeut("terapeut@gmail.com");

        assertThat(result)
                .extracting(PacientDTO::getNume)
                .containsExactly("A", "Popescu Alina", "Z");
    }

    @Test
    void getDetaliiPacient_shouldIncludeAllRelatedEntities() {
        TestUtils.saveTestEntities(evaluareRepository, evolutieRepository,
                diagnosticRepository, pacient, terapeut);

        List<Evaluare> evaluariSalvate = evaluareRepository.findAll();
        List<Evolutie> evolutiiSalvate = evolutieRepository.findAll();
        log.info("Evaluări salvate: {}", evaluariSalvate);
        log.info("Evoluții salvate: {}", evolutiiSalvate);

        PacientDetaliiDTO result = pacientService.getDetaliiPacient(pacient.getId());
        log.info("Evaluări returnate: {}", result.getEvaluari());
        log.info("Evoluții returnate: {}", result.getEvolutii());

        assertThat(result.getId()).isEqualTo(pacient.getId());
        assertThat(result.getEvaluari())
                .hasSize(1)
                .first()
                .satisfies(e -> {
                    assertThat(e.getTipEvaluare()).isEqualTo(TipEvaluare.INITIALA);
                    assertThat(e.getNumeDiagnostic()).isEqualTo("Diagnostic");
                });

        assertThat(result.getEvolutii())
                .hasSize(1)
                .first()
                .satisfies(e -> assertThat(e.getObservatii()).isEqualTo("Test observatii"));
    }

    @Test
    void getPacientHomeDTO_shouldMapCorrectData() {
        PacientHomeDTO result = pacientService.getPacientHomeDTO("pacient@gmail.com");
        assertThat(result.getNume()).isEqualTo("Popescu Alina");
        assertThat(result.getVarsta()).isEqualTo(Period.between(LocalDate.of(2000, 1, 1), LocalDate.now()).getYears());
    }

    @Test
    void getPacientByEmail_shouldReturnCorrectPacient() {
        Pacient result = pacientService.getPacientByEmail("pacient@gmail.com");
        assertThat(result.getId()).isEqualTo(pacient.getId());
    }

    @Test
    void getFormAction_shouldReturnCorrectPathBasedOnProgramare() {
        PacientHomeDTO dtoWithProgramare = new PacientHomeDTO();
        dtoWithProgramare.setUrmatoareaProgramare(new ProgramareDTO());
        PacientHomeDTO dtoWithoutProgramare = new PacientHomeDTO();
        assertThat(pacientService.getFormAction(dtoWithProgramare)).isEqualTo("/pacient/programari/modifica");
        assertThat(pacientService.getFormAction(dtoWithoutProgramare)).isEqualTo("/pacient/programari/creaza");
    }

    @Test
    void hasProgramare_shouldCheckProgramareExistence() {
        PacientHomeDTO dtoWithProgramare = new PacientHomeDTO();
        dtoWithProgramare.setUrmatoareaProgramare(new ProgramareDTO());
        PacientHomeDTO dtoWithoutProgramare = new PacientHomeDTO();
        assertThat(pacientService.hasProgramare(dtoWithProgramare)).isTrue();
        assertThat(pacientService.hasProgramare(dtoWithoutProgramare)).isFalse();
    }

    private int calculateExpectedAge() {
        return Period.between(pacient.getDataNastere(), LocalDate.now()).getYears();
    }
}
