package com.example.programari_service.mapper;

import com.example.programari_service.dto.EvaluareRequestDTO;
import com.example.programari_service.entity.Evaluare;
import org.springframework.stereotype.Component;

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
}
