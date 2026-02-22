package com.example.programari_service.service;

import com.example.programari_service.dto.UserDisplayCalendarDTO;
import com.example.programari_service.dto.statistici.*;
import com.example.programari_service.entity.StatusProgramare;
import com.example.programari_service.repository.ProgramareRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticiService {

    private final ProgramareRepository programareRepository;
    private final StatisticiCacheService statisticiCacheService;

    @Transactional(readOnly = true)
    public List<StatisticiProgramariLunareDTO> getProgramariLunare(LocalDate startDate, LocalDate endDate) {
        List<StatisticiProgramariLunareDTO> stats = programareRepository.countByLocatieIdAndMonth(
                startDate, endDate, StatusProgramare.ANULATA);
        Map<Long, String> locatiiMap = statisticiCacheService.getLocatiiMap();

        return stats.stream()
                .map(s -> new StatisticiProgramariLunareDTO(
                        s.locatieId(),
                        locatiiMap.getOrDefault(s.locatieId(), "Necunoscut"),
                        s.an(),
                        s.luna(),
                        s.count()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StatisticiVenituriLocatieDTO> getVenituriLocatie(LocalDate startDate, LocalDate endDate) {
        List<StatisticiVenituriLocatieDTO> stats = programareRepository.sumPretByLocatieIdAndStatus(
                startDate, endDate, StatusProgramare.FINALIZATA);
        Map<Long, String> locatiiMap = statisticiCacheService.getLocatiiMap();

        return stats.stream()
                .map(s -> new StatisticiVenituriLocatieDTO(
                        s.locatieId(),
                        locatiiMap.getOrDefault(s.locatieId(), "Necunoscut"),
                        s.totalVenituri()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StatisticiRataAnulareDTO> getRataAnulare(LocalDate startDate, LocalDate endDate) {
        // Raw data: [locatieId, count]
        List<Object[]> totalStatsRaw = programareRepository.countTotalByLocatieId(startDate, endDate);
        List<Object[]> anulateStatsRaw = programareRepository.countAnulateByLocatieId(
                startDate, endDate, StatusProgramare.ANULATA);
        
        Map<Long, String> locatiiMap = statisticiCacheService.getLocatiiMap();

        // Map locatieId -> count ANULATE
        Map<Long, Long> anulateMap = new HashMap<>();
        for (Object[] row : anulateStatsRaw) {
            Long locId = (Long) row[0];
            Long count = (Long) row[1];
            anulateMap.put(locId, count);
        }

        List<StatisticiRataAnulareDTO> result = new ArrayList<>();

        for (Object[] row : totalStatsRaw) {
            Long locId = (Long) row[0];
            Long totalCount = (Long) row[1];
            Long anulateCount = anulateMap.getOrDefault(locId, 0L);

            String numeLocatie = locatiiMap.getOrDefault(locId, "Necunoscut");

            double rata = 0.0;
            if (totalCount > 0) {
                rata = ((double) anulateCount / totalCount) * 100.0;
            }
            
            // Round to 2 decimals
            rata = Math.round(rata * 100.0) / 100.0;

            result.add(new StatisticiRataAnulareDTO(
                    locId,
                    numeLocatie,
                    rata
            ));
        }

        return result;
    }

    @Transactional(readOnly = true)
    public List<StatisticiPacientiNoiDTO> getPacientiNoi(LocalDate startDate, LocalDate endDate) {
        List<StatisticiPacientiNoiDTO> stats = programareRepository.countNewPatientsByLocatieIdAndMonth(startDate, endDate);
        Map<Long, String> locatiiMap = statisticiCacheService.getLocatiiMap();

        return stats.stream()
                .map(s -> new StatisticiPacientiNoiDTO(
                        s.locatieId(),
                        locatiiMap.getOrDefault(s.locatieId(), "Necunoscut"),
                        s.an(),
                        s.luna(),
                        s.numarPacientiNoi()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StatisticiTerapeutDTO> getProgramariTerapeut(LocalDate startDate, LocalDate endDate) {
        List<StatisticiTerapeutDTO> stats = programareRepository.countByTerapeutIdAndMonth(
                startDate, endDate, StatusProgramare.ANULATA);

        return stats.stream()
                .map(s -> {
                    UserDisplayCalendarDTO user = statisticiCacheService.getTerapeutUser(s.terapeutId());
                    if (user != null) {
                        return new StatisticiTerapeutDTO(
                                user.id(),
                                user.nume() + " " + user.prenume(),
                                s.count()
                        );
                    } else {
                        return new StatisticiTerapeutDTO(
                                s.terapeutId(),
                                "Necunoscut",
                                s.count()
                        );
                    }
                })
                .toList();
    }
}
