package com.vkr.tournament_service.controller.team;

import com.vkr.tournament_service.dto.team.TeamCreateDto;
import com.vkr.tournament_service.dto.team.TeamDto;
import com.vkr.tournament_service.service.team.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamController {
    private final TeamService teamService;

    @PostMapping("/{tournamentId}/create")
    public TeamDto createTeam(@RequestBody TeamCreateDto teamCreateDto, @PathVariable String tournamentId) {
        return teamService.createTeam(teamCreateDto, UUID.fromString(tournamentId));
    }

    @PostMapping("/{teamId}/add-player/{playerUsername}")
    public TeamDto addPlayerToTeam(@PathVariable String teamId, @PathVariable String playerUsername) {
        return teamService.addPlayerToTeam(UUID.fromString(teamId), playerUsername);
    }

    @GetMapping("/tournament/{tournamentId}")
    public Page<TeamDto> getAllTournamentTeams(@PathVariable String tournamentId, Pageable pageable) {
        return teamService.getAllTournamentTeams(UUID.fromString(tournamentId), pageable);
    }

    @GetMapping("/{teamId}")
    public TeamDto getTeam(@PathVariable String teamId) {
        return teamService.getTeam(UUID.fromString(teamId));
    }

    @DeleteMapping("/{teamId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete team")
    public void deleteTeam(@PathVariable String teamId, @RequestParam String userId) {
        teamService.deleteTeam(UUID.fromString(teamId), userId);
    }
}
