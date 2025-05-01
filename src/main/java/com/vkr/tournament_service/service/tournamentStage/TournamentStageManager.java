package com.vkr.tournament_service.service.tournamentStage;

import com.vkr.tournament_service.dto.team.TeamStandingsDto;
import com.vkr.tournament_service.entity.match.TournamentMatch;
import com.vkr.tournament_service.entity.team.TournamentTeam;
import com.vkr.tournament_service.entity.tournamentStage.TournamentStage;
import com.vkr.tournament_service.exception.EntityNotFoundException;
import com.vkr.tournament_service.repository.tournamentStage.TournamentStageRepository;
import com.vkr.tournament_service.validator.tournament.TournamentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TournamentStageManager {
    private final StageServiceFactory stageServiceFactory;
    private final TournamentStageRepository tournamentStageRepository;
    private final TournamentValidator tournamentValidator;

    public TournamentStage getStageById(UUID stageId) {
        return tournamentStageRepository.findById(stageId)
                .orElseThrow(() -> new EntityNotFoundException("Stage with id: " + stageId + " not found!"));
    }

    public void createStage(TournamentStage stage, List<TournamentTeam> teams) {
        StageService service = stageServiceFactory.getService(stage.getStageType());
        service.createStage(stage, teams);
    }

    public List<List<List<Map<String, Object>>>> getBracket(UUID stageId) {
        TournamentStage stage = getStageById(stageId);
        StageService service = stageServiceFactory.getService(stage.getStageType());
        return service.getBracket(stage);
    }

    public List<List<List<Map<String, Object>>>> updateBracket(List<List<List<Map<String, Object>>>> bracket,
                                                               UUID stageId, String userId) {
        TournamentStage stage = getStageById(stageId);
        tournamentValidator.validateAccess(stage.getTournament().getId(), userId);
        StageService service = stageServiceFactory.getService(stage.getStageType());
        return service.updateBracket(bracket, stage);
    }

    public void advanceTeam(TournamentMatch match) {
        TournamentStage stage = getStageById(match.getStage().getId());
        StageService service = stageServiceFactory.getService(stage.getStageType());
        service.advanceTeam(match);
    }

    public List<TournamentMatch> findParentMatches(TournamentMatch match) {
        TournamentStage stage = getStageById(match.getStage().getId());
        StageService service = stageServiceFactory.getService(stage.getStageType());
        return service.findParentMatches(match);
    }

    public List<TournamentMatch> findChildMatches(TournamentMatch match) {
        TournamentStage stage = getStageById(match.getStage().getId());
        StageService service = stageServiceFactory.getService(stage.getStageType());
        return service.findChildMatches(match);
    }

    public List<TeamStandingsDto> getCurrentStandings(UUID stageId) {
        TournamentStage stage = getStageById(stageId);
        StageService service = stageServiceFactory.getService(stage.getStageType());
        return service.getCurrentStandings(stage);
    }

    public boolean isStageFinished(UUID stageId) {
        TournamentStage stage = getStageById(stageId);
        StageService service = stageServiceFactory.getService(stage.getStageType());
        return service.isStageFinished(stage);
    }

    public List<TournamentTeam> getTeamsToNextStage(UUID stageId) {
        TournamentStage stage = getStageById(stageId);
        StageService service = stageServiceFactory.getService(stage.getStageType());
        return service.getTeamsToNextStage(stage);
    }
}
