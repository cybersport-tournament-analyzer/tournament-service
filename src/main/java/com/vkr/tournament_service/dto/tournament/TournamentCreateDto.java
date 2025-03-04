package com.vkr.tournament_service.dto.tournament;

import com.vkr.tournament_service.entity.tournament.Stage;
import com.vkr.tournament_service.entity.tournament.TournamentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Jacksonized
public class TournamentCreateDto {

    private String tournamentName;
    private String creatorUsername;
    private String tournamentFormat;
    private Long teamsCount;
    private List<String> stages;
}
