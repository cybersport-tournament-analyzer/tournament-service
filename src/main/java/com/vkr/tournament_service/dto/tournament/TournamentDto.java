package com.vkr.tournament_service.dto.tournament;

import com.vkr.tournament_service.entity.tournament.TournamentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TournamentDto {

    private UUID id;
    private String tournamentName;
    private String creatorUsername;
    private Long teamsCount;
    private String winnerTeamName;
    private LocalDateTime createdAt;
    private TournamentStatus tournamentStatus;
}
