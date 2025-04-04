package com.vkr.tournament_service.dto.tournament;

import com.vkr.tournament_service.dto.match.MatchDto;
import com.vkr.tournament_service.dto.team.TeamDto;
import com.vkr.tournament_service.dto.tournamentStage.TournamentStageDto;
import com.vkr.tournament_service.entity.tournament.TournamentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

// TournamentDto.java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Jacksonized
public class TournamentDto {
    private UUID id;
    private String tournamentName;
    private String creatorId;
    private Long teamsCount;
    private int substitutionsNumber;
    private String tournamentMode;
    private String winnerTeamName;
    private LocalDateTime createdAt;
    private OffsetDateTime registrationStartTime;
    private OffsetDateTime registrationEndTime;
    private OffsetDateTime tournamentStartTime;
    private TournamentStatus tournamentStatus;
    private Integer currentStageOrder;
    private List<TournamentStageDto> stages;
    private List<TeamDto> teams;
}
