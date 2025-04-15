package com.vkr.tournament_service.dto.team;

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
public class TeamStandingsDto {
    private TeamDto teamDto;
    private int place;
    private int wins;
    private int losses;
    private int points;
    private String groupLetter;
}
