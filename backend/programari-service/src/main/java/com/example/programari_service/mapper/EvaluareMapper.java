package com.example.programari_service.mapper;

import com.example.programari_service.dto.EvaluareRequestDTO;
import com.example.programari_service.entity.Evaluare;
import org.springframework.stereotype.Component;
import com.example.programari_service.dto.SituatiePacientDTO;

@Component
public class EvaluareMapper {
    public Evaluare toEntity(EvaluareRequestDTO request) {
        if (request == null) {
            return null;
        }

        Evaluare evaluare = new Evaluare();
        evaluare.setPacientId(request.getPacientId());
        evaluare.setTerapeutId(request.getTerapeutId());
        evaluare.setTip(request.getTip());
        evaluare.setDiagnostic(request.getDiagnostic());
        evaluare.setSedinteRecomandate(request.getSedinteRecomandate());
        evaluare.setServiciuRecomandatId(request.getServiciuRecomandatId());
        evaluare.setObservatii(request.getObservatii());

        return evaluare;
    }


    public SituatiePacientDTO toSituatiePacientDTO(Evaluare evaluare, long sedinteEfectuate) {
        if (evaluare == null) {
            return null;
        }

        long sedinteRamase = Math.max(0, evaluare.getSedinteRecomandate() - sedinteEfectuate);

        return SituatiePacientDTO.builder()
                .diagnostic(evaluare.getDiagnostic())
                .sedinteRecomandate(evaluare.getSedinteRecomandate())
                .sedinteEfectuate(sedinteEfectuate)
                .sedinteRamase(sedinteRamase)
                .build();
    }

    public SituatiePacientDTO toEmptySituatiePacientDTO() {
        return SituatiePacientDTO.builder()
                .diagnostic("Momentan indisponibil")
                .sedinteRecomandate(0)
                .sedinteEfectuate(0L)
                .sedinteRamase(0L)
                .build();
    }
}
