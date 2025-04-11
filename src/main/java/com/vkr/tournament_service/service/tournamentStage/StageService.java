package com.vkr.tournament_service.service.tournamentStage;

import com.vkr.tournament_service.entity.team.TournamentTeam;
import com.vkr.tournament_service.entity.tournamentStage.Stage;
import com.vkr.tournament_service.entity.tournamentStage.TournamentStage;

import java.util.List;
import java.util.Map;

public interface StageService {
    Stage getStageType();

    void createStage(TournamentStage stage, List<TournamentTeam> teams);

    List<List<List<Map<String, Object>>>> getBracket(TournamentStage stage);

    List<List<List<Map<String, Object>>>> updateBracket(List<List<List<Map<String, Object>>>> bracket, TournamentStage stage);
}
