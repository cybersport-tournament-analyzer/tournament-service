package com.vkr.tournament_service.dto.tournament;

import com.vkr.tournament_service.entity.tournament.Stage;
import com.vkr.tournament_service.entity.tournament.TournamentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TournamentUpdateDto {

    private String userId;
    private String tournamentName;
    private Long teamsCount;
    private int substitutionsNumber;
    private String winnerTeamName;
    private OffsetDateTime registrationStartTime;
    private OffsetDateTime registrationEndTime;
    private OffsetDateTime tournamentStartTime;
    private TournamentStatus tournamentStatus;
    private List<String> stages;
}
