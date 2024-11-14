package com.vkr.tournament_service.validator.tournament;

import java.util.UUID;

public interface TournamentValidator {

    void validateAccess(UUID tournamentId, String username);
}
