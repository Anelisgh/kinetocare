package com.example.kinetocare;

import com.example.kinetocare.domain.Pacient;
import com.example.kinetocare.domain.Programare;
import com.example.kinetocare.domain.Serviciu;
import com.example.kinetocare.domain.Terapeut;
import com.example.kinetocare.repository.*;
import com.example.kinetocare.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public abstract class AbstractIntegrationTest {
    @Autowired
    protected RegistrationService registrationService;

    @Autowired
    protected ProgramareService programareService;

    @Autowired
    protected PlataService plataService;

    @Autowired
    protected PacientService pacientService;

    @Autowired
    protected EvolutieService evolutieService;

    @Autowired
    protected EvaluareService evaluareService;

    @Autowired
    protected CalendarService calendarService;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected AuthorityRepository authorityRepository;

    @Autowired
    protected PacientRepository pacientRepository;

    @Autowired
    protected PlataRepository plataRepository;

    @Autowired
    protected TerapeutRepository terapeutRepository;

    @Autowired
    protected ProgramareRepository programareRepository;

    @Autowired
    protected ServiciuRepository serviciuRepository;

    @Autowired
    protected EvaluareRepository evaluareRepository;

    @Autowired
    protected DiagnosticRepository diagnosticRepository;

    @Autowired
    protected EvolutieRepository evolutieRepository;

    protected Pacient pacient;
    protected Terapeut terapeut;
    protected Serviciu serviciu;
    protected Programare programare;
    protected Serviciu serviciuKinetoterapie;
    protected Serviciu serviciuReevaluare;
}

