package com.vkr.tournament_service.dto.prediction;

import com.vkr.tournament_service.dto.tournamentStage.TournamentStageDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BracketPredictionDto {
    private String id;
    private String userId;
    private int score;
    private TournamentStageDto stage;
    private String predictedBracketJson;
    private String stageWinner;
    private String stageThirdPlace;
}

