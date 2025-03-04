package com.vkr.tournament_service.dto.tournament;

import com.vkr.tournament_service.entity.tournament.Stage;
import com.vkr.tournament_service.entity.tournament.TournamentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TournamentUpdateDto {

    private String tournamentName;
    private Long teamsCount;
    private String winnerTeamName;
    private TournamentStatus tournamentStatus;
    private List<String> stages;
}
