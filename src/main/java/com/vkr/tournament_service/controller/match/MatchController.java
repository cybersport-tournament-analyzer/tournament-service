package com.vkr.tournament_service.controller.match;

import com.vkr.tournament_service.service.match.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
