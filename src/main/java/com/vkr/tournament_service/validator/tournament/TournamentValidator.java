package com.vkr.tournament_service.validator.tournament;

import com.vkr.tournament_service.dto.tournament.TournamentCreateDto;
import com.vkr.tournament_service.dto.tournament.TournamentUpdateDto;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface TournamentValidator {

    void validateAccess(UUID tournamentId, String username);
    void validateTournamentTimes(OffsetDateTime registrationStart,
                                 OffsetDateTime registrationEnd,
                                 OffsetDateTime tournamentStart);
    void validateTimes(TournamentCreateDto dto);
    void validateTimes(TournamentUpdateDto dto);
}
