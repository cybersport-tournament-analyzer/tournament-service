package com.vkr.tournament_service.service.team;

import com.vkr.tournament_service.dto.team.TeamCreateDto;
import com.vkr.tournament_service.dto.team.TeamDto;
import com.vkr.tournament_service.entity.team.TournamentTeam;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface TeamService {

    TeamDto createTeam(TeamCreateDto teamCreateDto, UUID tournamentId);

    TeamDto addPlayerToTeam(UUID teamId, String playerUsername);

    TeamDto getTeam(UUID teamId);

    List<TeamDto> getAllTournamentTeams(UUID tournamentId);

    TournamentTeam getTeamByName(String teamName, UUID tournamentId);

    void deleteTeam(UUID teamId, String userId);
}
