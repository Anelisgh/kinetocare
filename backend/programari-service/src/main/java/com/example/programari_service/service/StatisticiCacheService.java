package com.example.programari_service.service;

import com.example.programari_service.client.TerapeutiClient;
import com.example.programari_service.client.UserClient;
import com.example.programari_service.dto.LocatieDisponibilaDTO;
import com.example.programari_service.dto.UserDisplayCalendarDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticiCacheService {

    private final TerapeutiClient terapeutiClient;
    private final UserClient userClient;

    // Helper pentru mapare locatieId -> locatieNume
    @Cacheable("locatii")
    public Map<Long, String> getLocatiiMap() {
        try {
            return terapeutiClient.getLocatii().stream()
                    .collect(Collectors.toMap(LocatieDisponibilaDTO::id, LocatieDisponibilaDTO::nume));
        } catch (Exception e) {
            log.error("Nu s-au putut prelua locatiile din terapeuti-service", e);
            return Map.of();
        }
    }

    // Helper pentru mapare terapeutId -> User curent (prin keycloakId)
    @Cacheable(value = "terapeutUser", key = "#terapeutId")
    public UserDisplayCalendarDTO getTerapeutUser(Long terapeutId) {
        try {
            String keycloakId = terapeutiClient.getKeycloakIdByTerapeutId(terapeutId);
            if (keycloakId != null) {
                return userClient.getUserByKeycloakId(keycloakId);
            }
        } catch (Exception e) {
            log.error("Nu s-au putut prelua datele userului pt terapeutId: {}", terapeutId, e);
        }
        return null;
    }
}
