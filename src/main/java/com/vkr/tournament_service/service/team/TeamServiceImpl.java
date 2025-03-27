package com.vkr.tournament_service.service.team;

import com.vkr.tournament_service.dto.player.PlayerCreateDto;
import com.vkr.tournament_service.dto.team.TeamCreateDto;
import com.vkr.tournament_service.dto.team.TeamDto;
import com.vkr.tournament_service.entity.player.Player;
import com.vkr.tournament_service.entity.team.TournamentTeam;
import com.vkr.tournament_service.entity.tournament.Tournament;
import com.vkr.tournament_service.entity.tournament.TournamentStatus;
import com.vkr.tournament_service.exception.*;
import com.vkr.tournament_service.mapper.team.TeamMapper;
import com.vkr.tournament_service.repository.team.TeamRepository;
import com.vkr.tournament_service.service.player.PlayerService;
import com.vkr.tournament_service.service.tournament.TournamentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;
    private final PlayerService playerService;
    private final TournamentService tournamentService;

    @Override
    @Transactional
    public TeamDto createTeam(TeamCreateDto teamCreateDto, UUID tournamentId) {
        Tournament currentTournament = tournamentService.getTournamentById(tournamentId);
        if (!currentTournament.getTournamentStatus().equals(TournamentStatus.REGISTRATION)) {
            throw new WrongTournamentStatusException("There is no registration on this tournament" +
                    " at the moment.");
        }
        if (currentTournament.getTeams().stream()
                .map(TournamentTeam::getTeamName)
                .anyMatch(name -> name.equals(teamCreateDto.getTeamName()))){
            throw new TeamNameAlreadyInUseException("Yours team name is already in use.");
        }
        if (currentTournament.getTeams().size() == currentTournament.getTeamsCount()) {
            throw new TeamListIsFullException("Tournament team list is full.");
        }
        log.info("Creating team: {}", teamCreateDto);

        TournamentTeam team = teamMapper.toEntity(teamCreateDto, tournamentId);
        team.setTournament(currentTournament);

        Player creatorPlayer = playerService.getPlayer(teamCreateDto.getCreatorSteamId());
        List<Player> players = new ArrayList<>();

        if (creatorPlayer != null) {
            if (isAlreadyInAnotherTeam(currentTournament, creatorPlayer)) {
                throw new AlreadyInOtherTeamException("Player with id " + creatorPlayer.getPlayerSteamId() +
                        " is already in another team");
            }
            players.add(creatorPlayer);
        } else {
            PlayerCreateDto dto = new PlayerCreateDto(teamCreateDto.getCreatorSteamId(),
                    new ArrayList<>());
            Player createdPlayer = playerService.createPlayer(dto);
            players.add(createdPlayer);
        }

        for (String id : teamCreateDto.getSteamIds()) {
            if (players.size() == currentTournament.getTeamPlayersNumber()){
                throw new TeamIsFullException("Maximum team size is: " +
                        currentTournament.getTeamPlayersNumber());
            }
            Player currentPlayer = playerService.getPlayer(id);
            if (currentPlayer != null) {
                if (isAlreadyInAnotherTeam(currentTournament, creatorPlayer) || players.contains(currentPlayer)) {
                    throw new AlreadyInOtherTeamException("Player with id " +  currentPlayer.getPlayerSteamId() +
                            " is already in another team");
                }
                players.add(currentPlayer);
            } else {
                PlayerCreateDto dto = new PlayerCreateDto(id, new ArrayList<>());
                Player createdPlayer = playerService.createPlayer(dto);
                players.add(createdPlayer);
            }
        }

        team.setPlayers(players);

        return teamMapper.toDto(teamRepository.save(team));
    }

    @Override
    public TeamDto addPlayerToTeam(UUID teamId, String playerSteamId) {
        TournamentTeam tournamentTeam = teamRepository.findById(teamId).orElseThrow();
//        tournamentTeam.getPlayers().add(playerRepository.findByPlayerSteamId(playerSteamId));
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

    private boolean isAlreadyInAnotherTeam(Tournament currentTournament, Player currentPlayer) {
        return currentTournament.getTeams().stream()
                .flatMap(t -> t.getPlayers().stream())
                .anyMatch(p -> p.getPlayerSteamId().equals(currentPlayer.getPlayerSteamId()));
    }
}
