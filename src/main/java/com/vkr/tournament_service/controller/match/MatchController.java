package com.vkr.tournament_service.controller.match;

import com.vkr.tournament_service.dto.match.MatchDto;
import com.vkr.tournament_service.dto.match.MatchRescheduleDto;
import com.vkr.tournament_service.service.match.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @PostMapping("/{tournamentId}/{matchId}/start")
    public void createMatch(@PathVariable String matchId, @PathVariable String tournamentId) {
        matchService.startTournamentMatch(UUID.fromString(matchId), UUID.fromString(tournamentId));
    }

    @PatchMapping("/{matchId}/rescheduleMatch")
    public MatchDto rescheduleMatch(@PathVariable String matchId, @RequestBody MatchRescheduleDto dto) {
        return matchService.rescheduleMatch(UUID.fromString(matchId), dto.getNewStartTime(), dto.getUserId());
    }
}
