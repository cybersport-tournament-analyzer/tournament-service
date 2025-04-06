package com.vkr.tournament_service.dto.tournamentStage;

import jakarta.validation.constraints.Min;
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
    @Min(1)
    private int stageOrder;
    @NotBlank
    private String stageType;
    @NotBlank
    private String finalMatchFormat;
    @NotBlank
    private String matchFormat;
    @NotBlank
    private boolean matchForTheThirdPlace;
}
