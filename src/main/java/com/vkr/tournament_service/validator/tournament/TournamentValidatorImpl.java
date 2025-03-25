package com.vkr.tournament_service.validator.tournament;

import com.vkr.tournament_service.dto.tournament.TournamentCreateDto;
import com.vkr.tournament_service.dto.tournament.TournamentUpdateDto;
import com.vkr.tournament_service.entity.tournament.Tournament;
import com.vkr.tournament_service.exception.InvalidTournamentTimeException;
import com.vkr.tournament_service.exception.ValidationException;
import com.vkr.tournament_service.repository.tournament.TournamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TournamentValidatorImpl implements TournamentValidator {

    private final TournamentRepository tournamentRepository;

    @Override
    public void validateAccess(UUID tournamentId, String userId) {
        Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow();
        if (!userId.equals(tournament.getCreatorId())){
            throw new ValidationException("User with id=" + userId + " has no access to tournament with name=" + tournament.getTournamentName());
        }
    }

    @Override
    public void validateTournamentTimes(OffsetDateTime registrationStart, OffsetDateTime registrationEnd, OffsetDateTime tournamentStart) {
        if (registrationStart.isBefore(OffsetDateTime.now())) {
            throw new InvalidTournamentTimeException("Registration start time must be after current time.");
        }
        if (registrationEnd.isBefore(registrationStart)) {
            throw new InvalidTournamentTimeException("Registration end time must be after registration start time.");
        }
        if (tournamentStart.isBefore(registrationEnd)) {
            throw new InvalidTournamentTimeException("Tournament start time must be after registration end time.");
        }
    }

    @Override
    public void validateTimes(TournamentCreateDto dto) {
        validateTournamentTimes(dto.getRegistrationStartTime(),
                dto.getRegistrationEndTime(),
                dto.getTournamentStartTime());
    }

    @Override
    public void validateTimes(TournamentUpdateDto dto) {
        validateTournamentTimes(dto.getRegistrationStartTime(),
                dto.getRegistrationEndTime(),
                dto.getTournamentStartTime());
    }
}
