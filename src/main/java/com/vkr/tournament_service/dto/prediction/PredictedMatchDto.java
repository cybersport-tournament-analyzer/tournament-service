package com.vkr.tournament_service.dto.prediction;

import lombok.Data;

import java.util.UUID;

@Data
public class PredictedMatchDto {
    private UUID matchId;
    private String predictedWinnerName;
}
