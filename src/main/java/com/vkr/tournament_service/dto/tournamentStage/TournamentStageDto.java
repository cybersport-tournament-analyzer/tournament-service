package com.vkr.tournament_service.dto.tournamentStage;

import com.vkr.tournament_service.dto.match.MatchDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Jacksonized
public class TournamentStageDto {
    private UUID id;
    private UUID tournamentId;
    private int stageOrder;
    private String stageType;
    private int currentRound;
    private int totalRounds;
    private String finalMatchFormat;
    private String matchFormat;
    private boolean matchForTheThirdPlace;
    private List<MatchDto> matches;
}
