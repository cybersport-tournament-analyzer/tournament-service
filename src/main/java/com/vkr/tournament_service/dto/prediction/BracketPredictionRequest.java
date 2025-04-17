package com.vkr.tournament_service.dto.prediction;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class BracketPredictionRequest {
    private String userId;
    private UUID stageId;
    private UUID tournamentId;
    private List<PredictedMatchDto> predictedMatches;
}

