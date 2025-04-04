package com.vkr.tournament_service.dto.match;

import com.vkr.tournament_service.dto.schedule.ScheduleDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Jacksonized
public class MatchDto {
    private UUID id;
    private UUID tournamentId;
    private String matchFormat;
    private Integer team1Score;
    private Integer team2Score;
    private Integer round;
    private Integer matchNumber;
    private String winnerTeamName;
    private String team1Name;
    private String team2Name;
    private ScheduleDto schedule;
}
