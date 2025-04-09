package com.vkr.tournament_service.dto.tournamentStage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Jacksonized
public class UpdateSingleEliminationBracketDto {
    private List<List<List<Map<String, Object>>>> bracket;
}
