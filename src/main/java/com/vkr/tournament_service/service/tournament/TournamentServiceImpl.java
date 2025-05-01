package com.vkr.tournament_service.service.tournament;

import com.vkr.tournament_service.dto.tournament.TournamentCreateDto;
import com.vkr.tournament_service.dto.tournament.TournamentDto;
import com.vkr.tournament_service.dto.tournament.TournamentUpdateDto;
import com.vkr.tournament_service.entity.team.TournamentTeam;
import com.vkr.tournament_service.entity.tournament.Tournament;
import com.vkr.tournament_service.entity.tournament.TournamentStatus;
import com.vkr.tournament_service.entity.tournamentStage.Stage;
import com.vkr.tournament_service.entity.tournamentStage.TournamentStage;
import com.vkr.tournament_service.exception.EntityNotFoundException;
import com.vkr.tournament_service.exception.WrongTournamentStatusException;
import com.vkr.tournament_service.mapper.tournament.TournamentMapper;
import com.vkr.tournament_service.repository.tournament.TournamentRepository;
import com.vkr.tournament_service.service.tournamentStage.TournamentStageManager;
import com.vkr.tournament_service.validator.tournament.TournamentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentServiceImpl implements TournamentService {

    private final TournamentRepository tournamentRepository;
    private final TournamentMapper tournamentMapper;
    private final TournamentValidator tournamentValidator;
    private final TournamentStageManager tournamentStageManager;

    @Override
    public TournamentDto getTournamentByName(String tournamentName) {
        Tournament tournament = getTournament(tournamentName);
        return tournamentMapper.toDto(tournament);
    }

    @Override
    public Page<TournamentDto> getAllTournaments(Pageable pageable) {
        return tournamentRepository.findAll(pageable).map(tournamentMapper::toDto);
    }

    @Override
    public TournamentDto createBaseTournament(TournamentCreateDto tournamentCreateDto) {
        tournamentValidator.validateTimes(tournamentCreateDto);

        Tournament tournament = tournamentMapper.toEntity(tournamentCreateDto);

        tournament.setTournamentStatus(TournamentStatus.NOT_STARTED);
        tournament.setCurrentStageOrder(0);


        List<TournamentStage> stages = IntStream.range(0, tournamentCreateDto.getStages().size())
                .mapToObj(i -> {
                    String stageName = tournamentCreateDto.getStages().get(i).getStageType();
                    return TournamentStage.builder()
                            .tournament(tournament)
                            .stageOrder(i + 1)
                            .stageType(Stage.fromName(stageName))
                            .finalMatchFormat(tournamentCreateDto.getStages().get(i).getFinalMatchFormat())
                            .matchFormat(tournamentCreateDto.getStages().get(i).getMatchFormat())
                            .matchForTheThirdPlace(tournamentCreateDto.getStages().get(i).isMatchForTheThirdPlace())
                            .matches(new ArrayList<>())
                            .numberOfGroups(tournamentCreateDto.getStages().get(i).getNumberOfGroups())
                            .teamsToAdvance(tournamentCreateDto.getStages().get(i).getTeamsToAdvance())
                            .totalRounds(tournamentCreateDto.getStages().get(i).getTotalRounds())
                            .build();
                })
                .collect(Collectors.toList());

        for (TournamentStage stage : stages) {
            Stage stageType = stage.getStageType();
            if (stageType.equals(Stage.SINGLE_ELIMINATION) || stageType.equals(Stage.DOUBLE_ELIMINATION)) {
                if (stage.getStageOrder() != tournament.getStages().size()) {
                    throw new WrongTournamentStatusException("Stage of type " + stageType + " must be the last in the tournament.");
                }
            }
        }

        tournament.setStages(stages);

        tournamentRepository.save(tournament);

        log.info("Tournament created: {}", tournament);

        return tournamentMapper.toDto(tournament);
    }

    @Override
    public TournamentDto updateTournament(TournamentUpdateDto tournamentUpdateDto, String tournamentId) {
        if (tournamentUpdateDto.getRegistrationEndTime() != null || tournamentUpdateDto.getRegistrationStartTime() != null
                || tournamentUpdateDto.getTournamentStartTime() != null) {
            tournamentValidator.validateTimes(tournamentUpdateDto);
        }


        Tournament tournament = getTournamentById(UUID.fromString(tournamentId));

        tournamentValidator.validateAccess(tournament.getId(), tournamentUpdateDto.getUserId());

        tournamentMapper.updateEntity(tournamentUpdateDto, tournament);

        tournamentRepository.save(tournament);

        log.info("Tournament updated: {}", tournament);

        return tournamentMapper.toDto(tournament);
    }

    @Override
    public void deleteTournament(String tournamentId, String userId) {
        Tournament tournament = getTournamentById(UUID.fromString(tournamentId));

        tournamentValidator.validateAccess(tournament.getId(), userId);

        tournamentRepository.delete(tournament);

        log.info("Tournament deleted: {}", tournament);
    }

    @Override
    public Tournament getTournamentById(UUID tournamentId) {
        return tournamentRepository.findById(tournamentId).orElseThrow(
                () -> new EntityNotFoundException("Tournament with id=" + tournamentId + " not found")
        );
    }

    @Override
    public TournamentDto startTournamentRegistration(String tournamentId, String userId) {
        tournamentValidator.validateAccess(UUID.fromString(tournamentId), userId);
        Tournament currentTournament = getTournamentById(UUID.fromString(tournamentId));
        if (!currentTournament.getTournamentStatus().equals(TournamentStatus.NOT_STARTED)) {
            throw new WrongTournamentStatusException("Registration is underway or has already " +
                    "been completed");
        }
        currentTournament.setTournamentStatus(TournamentStatus.REGISTRATION);
        tournamentRepository.save(currentTournament);
        return tournamentMapper.toDto(currentTournament);
    }

    @Override
    public TournamentDto stopTournamentRegistration(String tournamentId, String userId) {
        tournamentValidator.validateAccess(UUID.fromString(tournamentId), userId);
        Tournament currentTournament = getTournamentById(UUID.fromString(tournamentId));
        if (!currentTournament.getTournamentStatus().equals(TournamentStatus.REGISTRATION)) {
            throw new WrongTournamentStatusException("Registration has not started " +
                    "or has already passed");
        }
        currentTournament.setTournamentStatus(TournamentStatus.REGISTRATION_ENDED);
        setTeamsSeeds(currentTournament);
        startFirstStage(currentTournament);
        tournamentRepository.save(currentTournament);
        return tournamentMapper.toDto(currentTournament);
    }

    @Override
    public Tournament getTournament(String tournamentName) {
        return tournamentRepository.findByTournamentName(tournamentName).orElseThrow(
                () -> new EntityNotFoundException("Tournament with name=" + tournamentName + " not found")
        );
    }

    @Override
    public void startFirstStage(Tournament tournament) {
        TournamentStage stage = tournament.getStages().get(0);
        List<TournamentTeam> teams = tournament.getTeams();
        teams.sort(Comparator.comparingInt(TournamentTeam::getAverageRating).reversed());
        tournamentStageManager.createStage(stage, teams);
        tournament.setCurrentStageOrder(stage.getStageOrder());
        tournamentRepository.save(tournament);
    }

    @Override
    public void startNextStage(Tournament tournament, List<TournamentTeam> teams) {
        int currentTournamentStage = tournament.getCurrentStageOrder();
        if (tournament.getStages().size() == currentTournamentStage){
            tournament.setTournamentStatus(TournamentStatus.COMPLETED);
        }
        else{
            TournamentStage stage = tournament.getStages().get(currentTournamentStage + 1);
            tournamentStageManager.createStage(stage, teams);
            tournament.setCurrentStageOrder(stage.getStageOrder());
        }
        tournamentRepository.save(tournament);
    }

    @Override
    public void setTeamsSeeds(Tournament tournament) {
        List<TournamentTeam> teams = tournament.getTeams();
        if (teams.size() < 2) {
            throw new IllegalArgumentException("Not enough teams to create a tournament bracket.");
        }

        teams.sort(Comparator.comparingInt(TournamentTeam::getAverageRating).reversed());

        for (int i = 0; i < teams.size(); i++) {
            teams.get(i).setSeed(i + 1);
        }

        tournament.setTeams(teams);

        int totalTeams = teams.size();
        tournament.setTeamsCount((long) totalTeams);
        tournamentRepository.save(tournament);
    }
}
