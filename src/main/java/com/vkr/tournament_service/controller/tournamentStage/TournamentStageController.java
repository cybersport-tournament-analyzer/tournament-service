package com.vkr.tournament_service.controller.tournamentStage;

import com.vkr.tournament_service.dto.team.TeamStandingsDto;
import com.vkr.tournament_service.dto.tournamentStage.UpdateSingleEliminationBracketDto;
import com.vkr.tournament_service.service.tournamentStage.TournamentStageManager;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/stage")
@RequiredArgsConstructor
public class TournamentStageController {

    private final TournamentStageManager tournamentStageManager;

    @GetMapping("/{stageId}/bracket")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get bracket by stage id")
    public List<List<List<Map<String, Object>>>> getStageBracketById(@PathVariable String stageId) {
        return tournamentStageManager.getBracket(UUID.fromString(stageId));
    }

    @GetMapping("/{stageId}/standings")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get standings by stage id")
    public List<TeamStandingsDto> getTournamentStandingsById(@PathVariable String stageId) {
        return tournamentStageManager.getCurrentStandings(UUID.fromString(stageId));
    }

    @PatchMapping("/{stageId}/updateBracket")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update tournament bracket")
    public List<List<List<Map<String, Object>>>> updateTournamentBracket(@PathVariable String stageId, @RequestBody UpdateSingleEliminationBracketDto dto, @RequestParam String userId) {
        return tournamentStageManager.updateBracket(dto.getBracket(), UUID.fromString(stageId), userId);
    }
}
