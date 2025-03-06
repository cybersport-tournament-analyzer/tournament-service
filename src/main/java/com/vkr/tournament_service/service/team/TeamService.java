package com.vkr.tournament_service.service.team;

import com.vkr.tournament_service.dto.team.TeamCreateDto;
import com.vkr.tournament_service.dto.team.TeamDto;
import com.vkr.tournament_service.entity.match.Match;
import com.vkr.tournament_service.entity.team.TournamentTeam;
import com.vkr.tournament_service.entity.tournament.Tournament;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TeamService {

    TeamDto createTeam(TeamCreateDto teamCreateDto, UUID tournamentId);

    TeamDto addPlayerToTeam(UUID teamId, String playerUsername);

    TeamDto getTeam(UUID teamId);

    Page<TeamDto> getAllTournamentTeams(UUID tournamentId, Pageable pageable);

    TournamentTeam getTeamByName(String teamName, UUID tournamentId);
}
