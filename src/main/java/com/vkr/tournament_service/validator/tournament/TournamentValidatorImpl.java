package com.vkr.tournament_service.validator.tournament;

import com.vkr.tournament_service.entity.tournament.Tournament;
import com.vkr.tournament_service.exception.ValidationException;
import com.vkr.tournament_service.repository.tournament.TournamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TournamentValidatorImpl implements TournamentValidator {

    private final TournamentRepository tournamentRepository;

    @Override
    public void validateAccess(UUID tournamentId, String userId) {
        Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow();
        if (!userId.equals(String.valueOf(tournament.getCreatedAt()))){
            throw new ValidationException("User with id=" + userId + " has no access to tournament with name=" + tournament.getTournamentName());
        }
    }
}
