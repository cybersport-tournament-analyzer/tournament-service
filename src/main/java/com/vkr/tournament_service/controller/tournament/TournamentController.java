package com.vkr.tournament_service.controller.tournament;

import com.vkr.tournament_service.dto.tournament.TournamentCreateDto;
import com.vkr.tournament_service.dto.tournament.TournamentDto;
import com.vkr.tournament_service.dto.tournament.TournamentUpdateDto;
import com.vkr.tournament_service.service.tournament.TournamentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tournaments")
@RequiredArgsConstructor
public class TournamentController {

    private final TournamentService tournamentService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all tournaments")
    public Page<TournamentDto> getAllTournaments(Pageable pageable) {
        return tournamentService.getAllTournaments(pageable);
    }

    @GetMapping("/{tournamentName}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get tournament by name")
    public TournamentDto getTournamentByName(@PathVariable String tournamentName) {
        return tournamentService.getTournamentByName(tournamentName);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create tournament")
    public TournamentDto createTournament(@RequestBody TournamentCreateDto tournamentCreateDto) {
        return tournamentService.createBaseTournament(tournamentCreateDto);
    }

    @PatchMapping("/{tournamentId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update tournament")
    public TournamentDto updateTournament(@PathVariable String tournamentId, @RequestBody TournamentUpdateDto tournamentUpdateDto) {
        return tournamentService.updateTournament(tournamentUpdateDto, tournamentId);
    }

    @DeleteMapping("/{tournamentId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete tournament")
    public void deleteTournament(@PathVariable String tournamentId, @RequestParam String userId) {
        tournamentService.deleteTournament(tournamentId, userId);
    }
}
