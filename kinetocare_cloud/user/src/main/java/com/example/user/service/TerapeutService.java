package com.example.user.service;

import com.example.common.dto.TerapeutDTO;
import com.example.user.domain.Terapeut;
import com.example.user.mapper.TerapeutMapper;
import com.example.user.repository.TerapeutRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TerapeutService {
    private final TerapeutRepository terapeutRepository;
    private final TerapeutMapper terapeutMapper;

    public TerapeutDTO getTerapeutByEmail(String email) {
        Terapeut terapeut = terapeutRepository.findByUserEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Terapeutul nu a fost găsit"));
        return terapeutMapper.toDTO(terapeut);
    }

    public TerapeutDTO getTerapeutDtoById(Long id) {
        Terapeut terapeut = terapeutRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Terapeutul nu a fost găsit"));
        return terapeutMapper.toDTO(terapeut);
    }
}
