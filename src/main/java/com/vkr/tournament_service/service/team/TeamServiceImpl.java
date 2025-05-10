package com.vkr.tournament_service.service.team;

import com.vkr.tournament_service.client.user.UserClient;
import com.vkr.tournament_service.dto.player.PlayerCreateDto;
import com.vkr.tournament_service.dto.team.TeamCreateDto;
import com.vkr.tournament_service.dto.team.TeamCreatePlayersDto;
import com.vkr.tournament_service.dto.team.TeamDto;
import com.vkr.tournament_service.dto.user.GetAverageRatingByIdsDto;
import com.vkr.tournament_service.entity.player.InGameRole;
import com.vkr.tournament_service.entity.player.Player;
import com.vkr.tournament_service.entity.team.TournamentTeam;
import com.vkr.tournament_service.entity.tournament.Tournament;
import com.vkr.tournament_service.entity.tournament.TournamentStatus;
import com.vkr.tournament_service.exception.*;
import com.vkr.tournament_service.mapper.team.TeamMapper;
import com.vkr.tournament_service.repository.team.TeamRepository;
import com.vkr.tournament_service.service.player.PlayerService;
import com.vkr.tournament_service.service.tournament.TournamentService;
import com.vkr.tournament_service.validator.team.TeamValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;
    private final PlayerService playerService;
    private final TournamentService tournamentService;
    private final UserClient userClient;
    private final TeamValidator teamValidator;

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
                .anyMatch(name -> name.equals(teamCreateDto.getTeamName()))) {
            throw new TeamNameAlreadyInUseException("Yours team name is already in use.");
        }
        if (currentTournament.getTeams().size() == currentTournament.getTeamsCount()) {
            throw new TeamListIsFullException("Tournament team list is full.");
        }
        log.info("Creating team: {}", teamCreateDto);

        TournamentTeam team = teamMapper.toEntity(teamCreateDto, tournamentId);
        team.setTournament(currentTournament);

        List<Player> players = new ArrayList<>();
        PlayerCreateDto dto = new PlayerCreateDto(teamCreateDto.getCreatorSteamId(), InGameRole.fromString(teamCreateDto.getInGameRole()));
        Player createdTeamCreatorPlayer = playerService.createPlayer(dto);
        if (isAlreadyInAnotherTeam(currentTournament, createdTeamCreatorPlayer)) {
            throw new AlreadyInOtherTeamException("Player with id " + createdTeamCreatorPlayer.getPlayerSteamId() +
                    " is already in another team");
        }
        players.add(createdTeamCreatorPlayer);

        for (TeamCreatePlayersDto teamCreatePlayersDto : teamCreateDto.getPlayers()) {
            if (players.size() == currentTournament.getTeamPlayersNumber()) {
                throw new TeamIsFullException("Maximum team size is: " +
                        currentTournament.getTeamPlayersNumber());
            }
            PlayerCreateDto playerDto = new PlayerCreateDto(teamCreatePlayersDto.getSteamId(), InGameRole.fromString(teamCreatePlayersDto.getInGameRole()));
            Player createdPlayer = playerService.createPlayer(playerDto);
            if (isAlreadyInAnotherTeam(currentTournament, createdPlayer) || players.contains(createdPlayer)) {
                throw new AlreadyInOtherTeamException("Player with id " + createdPlayer.getPlayerSteamId() +
                        " is already in another team");
            }
            players.add(createdPlayer);
        }

        if (players.size() < currentTournament.getTeamPlayersNumber() - currentTournament.getSubstitutionsNumber()) {
            throw new TeamIsFullException("Minimum team size is: " +
                    (currentTournament.getTeamPlayersNumber() - currentTournament.getSubstitutionsNumber()));
        }

        team.setPlayers(players);
        team.setAverageRating(getAverageEloRating(players,
                currentTournament.getTeamPlayersNumber() - currentTournament.getSubstitutionsNumber()));

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
    public List<TeamDto> getAllTournamentTeams(UUID tournamentId) {
        return teamRepository.findAllByTournamentId(tournamentId).stream()
                .map(teamMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public TournamentTeam getTeamByName(String teamName, UUID tournamentId) {
        return teamRepository.findByTeamNameAndTournamentId(teamName, tournamentId);
    }

    @Override
    public void deleteTeam(UUID teamId, String userId) {
        TournamentTeam team = teamRepository.findById(teamId).orElseThrow();
        teamValidator.validateAccess(teamId, userId);
        teamRepository.delete(team);
    }

    @Override
    @Transactional
    public TeamDto updateMainRoster(UUID teamId, List<String> newMainRosterPlayerIds, String userId) {
        teamValidator.validateAccess(teamId, userId);
        TournamentTeam team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Team not found with id=" + teamId));
        if (newMainRosterPlayerIds.size() != team.getTournament().getTeamPlayersNumber() - team.getTournament().getSubstitutionsNumber()) {
            throw new IllegalArgumentException("Main roster must contain exactly "
                    + (team.getTournament().getTeamPlayersNumber()
                    - team.getTournament().getSubstitutionsNumber()) + " players");
        }
        List<Player> allPlayers = team.getPlayers();

        Set<String> allPlayerIds = allPlayers.stream().map(Player::getPlayerSteamId).collect(Collectors.toSet());
        if (!allPlayerIds.containsAll(newMainRosterPlayerIds)) {
            throw new IllegalArgumentException("Some players are not part of the team");
        }

        List<Player> newPlayersOrder = new ArrayList<>();

        // Добавляем основной состав в указанном порядке
        for (String playerId : newMainRosterPlayerIds) {
            allPlayers.stream()
                    .filter(p -> p.getPlayerSteamId().equals(playerId))
                    .findFirst()
                    .ifPresent(newPlayersOrder::add);
        }

        // Добавляем оставшихся (замен)
        allPlayers.stream()
                .filter(p -> !newMainRosterPlayerIds.contains(p.getPlayerSteamId()))
                .forEach(newPlayersOrder::add);

        team.setPlayers(newPlayersOrder);
        return teamMapper.toDto(teamRepository.save(team));
    }

    private boolean isAlreadyInAnotherTeam(Tournament currentTournament, Player currentPlayer) {
        return currentTournament.getTeams().stream()
                .flatMap(t -> t.getPlayers().stream())
                .anyMatch(p -> p.getPlayerSteamId().equals(currentPlayer.getPlayerSteamId()));
    }

    private int getAverageEloRating(List<Player> players, int playersNumber) {
        GetAverageRatingByIdsDto dto = new GetAverageRatingByIdsDto();
        dto.setPlayersNumber(playersNumber);
        List<String> steamIds = players.stream()
                .map(Player::getPlayerSteamId)
                .toList();
        dto.setIds(steamIds);

        return userClient.getAverageRatingByIds(dto);
    }
}
