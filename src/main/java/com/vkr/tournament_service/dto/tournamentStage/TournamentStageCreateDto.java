package com.vkr.tournament_service.dto.tournamentStage;

import jakarta.validation.constraints.NotBlank;
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
public class TournamentStageCreateDto {
    @NotBlank
    private String stageType;
    private String finalMatchFormat;
    @NotBlank
    private String matchFormat;
    private boolean matchForTheThirdPlace;
    private int numberOfGroups;
    private int teamsToAdvance;
    private int totalRounds;
}
