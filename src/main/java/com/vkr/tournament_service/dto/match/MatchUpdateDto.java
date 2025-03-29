package com.vkr.tournament_service.dto.match;

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
public class MatchUpdateDto {
    private Integer team1Score;
    private Integer team2Score;
    private String winnerTeamName;
    private String matchStatus;
}
