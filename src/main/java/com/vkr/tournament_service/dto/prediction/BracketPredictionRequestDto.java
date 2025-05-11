package com.vkr.tournament_service.dto.prediction;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class BracketPredictionRequestDto {
    private String stageId;
    private String userId;
    private List<List<List<Map<String, Object>>>> bracket;
    private String stageWinner;
    private String stageThirdPlace;
}
