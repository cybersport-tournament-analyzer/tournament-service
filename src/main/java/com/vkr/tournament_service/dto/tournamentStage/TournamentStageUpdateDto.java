package com.vkr.tournament_service.dto.tournamentStage;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Jacksonized
public class TournamentStageUpdateDto {
    @Min(1)
    private Integer stageOrder;
    private String stageType;
}
