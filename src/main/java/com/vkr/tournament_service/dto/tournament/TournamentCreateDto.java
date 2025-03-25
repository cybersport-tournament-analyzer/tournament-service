package com.vkr.tournament_service.dto.tournament;

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
public class TournamentCreateDto {

    private String tournamentName;
    private String creatorId;
    private String tournamentMode;
    private Long teamsCount;
    private OffsetDateTime registrationStartTime;
    private OffsetDateTime registrationEndTime;
    private OffsetDateTime tournamentStartTime;
    private List<String> stages;
}
