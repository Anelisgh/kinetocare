package com.example.kinetocare.service;

import com.example.kinetocare.domain.*;
import com.example.kinetocare.domain.security.Authority;
import com.example.kinetocare.domain.security.RoleType;
import com.example.kinetocare.domain.security.User;
import com.example.kinetocare.dto.*;
import com.example.kinetocare.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class TestUtils {
    public static void clearDatabase(ProgramareRepository programareRepository, PlataRepository plataRepository, UserRepository userRepository, AuthorityRepository authorityRepository, ServiciuRepository serviciuRepository, DiagnosticRepository diagnosticRepository, EvaluareRepository evaluareRepository, PacientRepository pacientRepository, TerapeutRepository terapeutRepository, EvolutieRepository evolutieRepository) {
        evaluareRepository.deleteAll();
        evolutieRepository.deleteAll();
        diagnosticRepository.deleteAll();
        pacientRepository.deleteAll();
        programareRepository.deleteAll();
        plataRepository.deleteAll();
        terapeutRepository.deleteAll();
        userRepository.deleteAll();
        authorityRepository.deleteAll();
        serviciuRepository.deleteAll();
    }

    public static User createAndSaveUser(RegistrationDTO dto,
                                         UserRepository userRepo,
                                         AuthorityRepository authorityRepo) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());

        Authority authority = authorityRepo.findByRoleType(dto.getRoleType())
                .orElseGet(() -> createAndSaveAuthority(dto.getRoleType(), authorityRepo));

        user.getAuthorities().add(authority);
        return userRepo.save(user);
    }

    private static Authority createAndSaveAuthority(RoleType roleType, AuthorityRepository repo) {
        Authority newAuth = new Authority();
        newAuth.setRoleType(roleType);
        return repo.save(newAuth);
    }

    public static RegistrationDTO createPacientRegistrationDTO() {
        RegistrationDTO dto = new RegistrationDTO();
        dto.setEmail("pacient@gmail.com");
        dto.setPassword("123456");
        dto.setRoleType(RoleType.ROLE_PACIENT);

        PacientDetails details = new PacientDetails();
        details.setNume("Popescu Alina");
        details.setTelefon("0712345678");
        details.setCnp("1234567890123");
        details.setGen(Gen.F);
        details.setDataNastere(LocalDate.of(2000, 1, 1));
        details.setTipSport(TipSport.NU_FAC_SPORT);

        dto.setPacientDetails(details);

        return dto;
    }

    public static RegistrationDTO createTerapeutRegistrationDTO() {
        RegistrationDTO dto = new RegistrationDTO();
        dto.setEmail("terapeut@gmail.com");
        dto.setPassword("abcdef");
        dto.setRoleType(RoleType.ROLE_TERAPEUT);

        TerapeutDetails details = new TerapeutDetails();
        details.setNume("Ionescu Vlad");
        details.setTelefon("0723456789");
        details.setCnp("9876543210123");
        details.setDataNastere(LocalDate.of(1990, 1, 1));

        dto.setTerapeutDetails(details);

        return dto;
    }

    public static Terapeut createAndSaveTerapeut(RegistrationDTO dto,
                                                 UserRepository userRepo,
                                                 AuthorityRepository authorityRepo,
                                                 TerapeutRepository terapeutRepo) {
        User user = createAndSaveUser(dto, userRepo, authorityRepo);
        Terapeut terapeut = new Terapeut();
        terapeut.setUser(user);
        terapeut.setNume(dto.getTerapeutDetails().getNume());
        return terapeutRepo.save(terapeut);
    }

    public static Pacient createAndSavePacient(RegistrationDTO dto,
                                               Terapeut terapeut,
                                               UserRepository userRepo,
                                               AuthorityRepository authorityRepo,
                                               PacientRepository pacientRepo) {
        User user = createAndSaveUser(dto, userRepo, authorityRepo);
        Pacient pacient = new Pacient();
        pacient.setUser(user);
        pacient.setTerapeut(terapeut);
        pacient.setNume(dto.getPacientDetails().getNume());
        pacient.setDataNastere(dto.getPacientDetails().getDataNastere());
        return pacientRepo.save(pacient);
    }

    public static Evaluare createEvaluareTest(Pacient pacient, Diagnostic diagnostic) {
        Evaluare evaluare = new Evaluare();
        evaluare.setPacient(pacient);
        evaluare.setDiagnostic(diagnostic);
        evaluare.setData(LocalDate.now());
        evaluare.setTipEvaluare(TipEvaluare.INITIALA);
        return evaluare;
    }

    public static Evolutie createEvolutieTest(Pacient pacient) {
        Evolutie evolutie = new Evolutie();
        evolutie.setPacient(pacient);
        evolutie.setData(LocalDate.now());
        evolutie.setObservatii("Test observatii");
        return evolutie;
    }

    public static Diagnostic createDiagnosticTest() {
        Diagnostic diagnostic = new Diagnostic();
        diagnostic.setNume( "Diagnostic");
        diagnostic.setSedinteRecomandate(10);
        diagnostic.setData(LocalDate.now());
        return diagnostic;
    }

    public static void saveTestEntities(EvaluareRepository evaluareRepo,
                                        EvolutieRepository evolutieRepo,
                                        DiagnosticRepository diagnosticRepo,
                                        Pacient pacient,
                                        Terapeut terapeut) {
        Diagnostic diagnostic = createDiagnosticTest();
        diagnostic.setPacient(pacient);
        diagnostic.setTerapeut(terapeut);
        diagnostic = diagnosticRepo.save(diagnostic);

        Evaluare evaluare = createEvaluareTest(pacient, diagnostic);
        evaluare.setTerapeut(terapeut);
        evaluareRepo.save(evaluare);

        Evolutie evolutie = createEvolutieTest(pacient);
        evolutie.setTerapeut(terapeut);
        evolutieRepo.save(evolutie);
    }
    public static EvaluareDTO createValidEvaluareDTO(Long pacientId) {
        return EvaluareDTO.builder()
                .pacientId(pacientId)
                .tipEvaluare(TipEvaluare.INITIALA)
                .dataEvaluare(LocalDate.now())
                .numeDiagnostic("Diagnostic")
                .sedinteRecomandate(10)
                .tipServiciu(TipServiciu.MASAJ)
                .build();
    }
    public static EvolutieDTO createValidEvolutieDTO(Long pacientId) {
        return EvolutieDTO.builder()
                .pacientId(pacientId)
                .dataEvolutie(LocalDate.now())
                .observatii("Îmbunătățire semnificativă")
                .build();
    }

    public static Serviciu createServiciu(TipServiciu tip, int durata) {
        return Serviciu.builder()
                .tipServiciu(tip)
                .durataMinute(durata)
                .pret(BigDecimal.valueOf(100))
                .build();
    }

    public static ProgramareDTO createValidProgramareDTO(LocalDate data, LocalTime ora) {
        return ProgramareDTO.builder()
                .data(data)
                .ora(ora)
                .build();
    }

    public static ProgramareDTO createValidProgramareDTO(Long id, LocalDate data, LocalTime ora) {
        return ProgramareDTO.builder()
                .id(id)
                .data(data)
                .ora(ora)
                .build();
    }

    public static Programare createProgramare(Terapeut terapeut,
                                              Pacient pacient,
                                              LocalDate data,
                                              LocalTime ora,
                                              Serviciu serviciu,
                                              ProgramareRepository repo) {
        Programare programare = Programare.builder()
                .data(data)
                .ora(ora)
                .terapeut(terapeut)
                .pacient(pacient)
                .serviciu(serviciu)
                .status(Status.PROGRAMATA)
                .build();
        return repo.save(programare);
    }

    public static Programare createProgramareFinalizata(Pacient pacient,
                                                        Terapeut terapeut,
                                                        Serviciu serviciu,
                                                        ProgramareRepository repo) {
        Programare programare = Programare.builder()
                .data(LocalDate.now().minusDays(1))
                .ora(LocalTime.of(10, 0))
                .terapeut(terapeut)
                .pacient(pacient)
                .serviciu(serviciu)
                .status(Status.FINALIZATA)
                .build();
        pacient.getProgramari().add(programare);
        repo.save(programare);
        return programare;
    }

    public static Plata createPlata(Programare programare, BigDecimal suma, StarePlata stare, PlataRepository plataRepository) {
        Plata plata = Plata.builder()
                .data(LocalDate.now())
                .suma(suma)
                .starePlata(stare)
                .programare(programare)
                .pacient(programare.getPacient())
                .build();
        return plataRepository.save(plata);
    }
}