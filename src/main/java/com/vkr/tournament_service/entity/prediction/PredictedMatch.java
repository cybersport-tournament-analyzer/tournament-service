package com.vkr.tournament_service.entity.prediction;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.util.UUID;

@Embeddable
@Data
public class PredictedMatch {

    private UUID matchId;

    private String predictedWinnerName;
}

