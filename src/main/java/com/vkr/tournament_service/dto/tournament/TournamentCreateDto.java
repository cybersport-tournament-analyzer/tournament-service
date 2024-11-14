package com.vkr.tournament_service.dto.tournament;

import com.vkr.tournament_service.entity.tournament.TournamentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TournamentCreateDto {

    private String tournamentName;
    private String creatorUsername;
    private Long teamsCount;
}
