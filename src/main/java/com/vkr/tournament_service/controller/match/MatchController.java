package com.vkr.tournament_service.controller.match;

import com.vkr.tournament_service.dto.match.MatchCreateDto;
import com.vkr.tournament_service.service.match.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @PostMapping("/{tournamentId}")
    public void createMatch(@RequestBody MatchCreateDto matchCreateDto, @PathVariable String tournamentId) {
        matchService.startTournamentMatch(matchCreateDto, UUID.fromString(tournamentId));
    }
}
