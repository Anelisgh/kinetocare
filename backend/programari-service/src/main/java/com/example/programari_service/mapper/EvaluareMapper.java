package com.example.programari_service.mapper;

import com.example.programari_service.dto.EvaluareRequestDTO;
import com.example.programari_service.dto.SituatiePacientDTO;
import com.example.programari_service.entity.Evaluare;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EvaluareMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "programareId", ignore = true)
    @Mapping(target = "data", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Evaluare toEntity(EvaluareRequestDTO request);

    default SituatiePacientDTO toSituatiePacientDTO(Evaluare evaluare, long sedinteEfectuate) {
        if (evaluare == null) {
            return null;
        }

        long sedinteRamase = Math.max(0, evaluare.getSedinteRecomandate() - sedinteEfectuate);

        return new SituatiePacientDTO(
                evaluare.getDiagnostic(),
                evaluare.getSedinteRecomandate(),
                sedinteEfectuate,
                sedinteRamase
        );
    }

    default SituatiePacientDTO toEmptySituatiePacientDTO() {
        return new SituatiePacientDTO(
                "Momentan indisponibil",
                0,
                0L,
                0L
        );
    }
}
