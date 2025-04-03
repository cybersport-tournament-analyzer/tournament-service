package com.vkr.tournament_service;

import com.vkr.tournament_service.dto.team.TeamCreateDto;
import com.vkr.tournament_service.dto.team.TeamDto;
import com.vkr.tournament_service.entity.player.Player;
import com.vkr.tournament_service.entity.team.TournamentTeam;
import com.vkr.tournament_service.mapper.team.TeamMapper;
import com.vkr.tournament_service.repository.player.PlayerRepository;
import com.vkr.tournament_service.repository.team.TeamRepository;
import com.vkr.tournament_service.service.team.TeamServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTests {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamMapper teamMapper;

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private TeamServiceImpl teamService;

    private TournamentTeam team;
    private TeamDto teamDto;
    private TeamCreateDto teamCreateDto;
    private Player player;
    private UUID tournamentId;
    private UUID teamId;
    private UUID playerId;

    @BeforeEach
    void setUp() {
        tournamentId = UUID.randomUUID();
        teamId = UUID.randomUUID();
        playerId = UUID.randomUUID();

        team = new TournamentTeam();
        team.setId(teamId);
        team.setTeamName("Test Team");

        teamDto = new TeamDto();
        teamDto.setTeamName("Test Team");

        teamCreateDto = new TeamCreateDto();
        teamCreateDto.setCreatorUsername("player1");

        player = new Player();
        player.setId(playerId);
    }

    @Test
    void createTeam_ShouldCreateAndReturnTeam() {
        when(teamMapper.toEntity(any(TeamCreateDto.class), any(UUID.class))).thenReturn(team);
        when(playerRepository.findByPlayerUsername("player1")).thenReturn(player);
        when(teamRepository.save(any(TournamentTeam.class))).thenReturn(team);
        when(teamMapper.toDto(any(TournamentTeam.class))).thenReturn(teamDto);

        TeamDto result = teamService.createTeam(teamCreateDto, tournamentId);

        assertNotNull(result);
        assertEquals("Test Team", result.getTeamName());
        verify(teamRepository, times(1)).save(any(TournamentTeam.class));
    }


    @Test
    void getTeam_ShouldReturnTeam() {
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(teamMapper.toDto(any(TournamentTeam.class))).thenReturn(teamDto);

        TeamDto result = teamService.getTeam(teamId);

        assertNotNull(result);
        assertEquals("Test Team", result.getTeamName());
    }

    @Test
    void getTeamByName_ShouldReturnTeam() {
        when(teamRepository.findByTeamNameAndTournamentId("Test Team", tournamentId)).thenReturn(team);

        TournamentTeam result = teamService.getTeamByName("Test Team", tournamentId);

        assertNotNull(result);
        assertEquals("Test Team", result.getTeamName());
    }
}

