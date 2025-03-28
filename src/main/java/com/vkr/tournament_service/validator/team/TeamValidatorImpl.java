package com.vkr.tournament_service.validator.team;

import com.vkr.tournament_service.entity.team.TournamentTeam;
import com.vkr.tournament_service.exception.ValidationException;
import com.vkr.tournament_service.repository.team.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TeamValidatorImpl implements TeamValidator{

    private final TeamRepository teamRepository;

    @Override
    public void validateAccess(UUID teamId, String userId) {
        TournamentTeam team = teamRepository.findById(teamId).orElseThrow();
        if (!userId.equals(team.getCreatorSteamId()) && !userId.equals(team.getTournament().getCreatorId())){
            throw new ValidationException("User with id=" + userId + " is not a team or tournament creator.");
        }
    }
}
