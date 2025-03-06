package com.vkr.tournament_service.dto.tournament;

import com.vkr.tournament_service.dto.match.MatchDto;
import com.vkr.tournament_service.dto.team.TeamDto;
import com.vkr.tournament_service.entity.match.TournamentMatch;
import com.vkr.tournament_service.entity.team.TournamentTeam;
import com.vkr.tournament_service.entity.tournament.Stage;
import com.vkr.tournament_service.entity.tournament.TournamentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Jacksonized
public class TournamentDto {

    private UUID id;
    private String tournamentName;
    private String creatorUsername;
    private String tournamentMode;
    private Long teamsCount;
    private String winnerTeamName;
    private LocalDateTime createdAt;
    private TournamentStatus tournamentStatus;
    private List<String> stages;
    private Integer currentStageNumber;
    private List<MatchDto> matches = new ArrayList<>();
    private List<TeamDto> teams = new ArrayList<>();
}
