package com.vkr.tournament_service.dto.tournament;

import com.vkr.tournament_service.dto.tournamentStage.TournamentStageUpdateDto;
import com.vkr.tournament_service.entity.tournament.TournamentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.time.OffsetDateTime;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Jacksonized
public class TournamentUpdateDto {
    private String userId;
    private String tournamentName;
    private Long teamsCount;
    private Integer substitutionsNumber;
    private String tournamentMode;
    private String description;
    private OffsetDateTime registrationStartTime;
    private OffsetDateTime registrationEndTime;
    private OffsetDateTime tournamentStartTime;
    private TournamentStatus tournamentStatus;
    private Integer currentStageOrder;
    private List<TournamentStageUpdateDto> stages;
}
