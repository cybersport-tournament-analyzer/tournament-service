package com.vkr.tournament_service.service.team;

import com.vkr.tournament_service.dto.team.TeamCreateDto;
import com.vkr.tournament_service.dto.team.TeamDto;
import com.vkr.tournament_service.entity.player.Player;
import com.vkr.tournament_service.entity.team.TournamentTeam;
import com.vkr.tournament_service.mapper.team.TeamMapper;
import com.vkr.tournament_service.repository.player.PlayerRepository;
import com.vkr.tournament_service.repository.team.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;
    private final PlayerRepository playerRepository;

    @Override
    public TeamDto createTeam(TeamCreateDto teamCreateDto, UUID tournamentId) {
        log.info("Creating team: {}", teamCreateDto);

        TournamentTeam team = teamMapper.toEntity(teamCreateDto, tournamentId);

        Player creatorPlayer = playerRepository.findByPlayerUsername(teamCreateDto.getCreatorUsername());

        if (creatorPlayer != null) {
            team.setPlayers(List.of(creatorPlayer));
        }

        return teamMapper.toDto(teamRepository.save(team));
    }

    @Override
    public TeamDto addPlayerToTeam(UUID teamId, String playerUsername) {
        TournamentTeam tournamentTeam = teamRepository.findById(teamId).orElseThrow();
        tournamentTeam.getPlayers().add(playerRepository.findByPlayerUsername(playerUsername));
        return teamMapper.toDto(teamRepository.save(tournamentTeam));
    }

    @Override
    public TeamDto getTeam(UUID teamId) {
        return teamMapper.toDto(teamRepository.findById(teamId).orElseThrow());
    }

    @Override
    public Page<TeamDto> getAllTournamentTeams(UUID tournamentId, Pageable pageable) {
        return teamRepository.findAllByTournamentId(tournamentId, pageable).map(teamMapper::toDto);
    }

    @Override
    public TournamentTeam getTeamByName(String teamName, UUID tournamentId) {
        return teamRepository.findByTeamNameAndTournamentId(teamName, tournamentId);
    }
}
