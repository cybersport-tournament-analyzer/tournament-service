package com.vkr.tournament_service;

import com.vkr.tournament_service.dto.tournament.TournamentCreateDto;
import com.vkr.tournament_service.dto.tournament.TournamentDto;
import com.vkr.tournament_service.dto.tournament.TournamentUpdateDto;
import com.vkr.tournament_service.entity.tournament.Stage;
import com.vkr.tournament_service.entity.tournament.Tournament;
import com.vkr.tournament_service.entity.tournament.TournamentStatus;
import com.vkr.tournament_service.exception.EntityNotFoundException;
import com.vkr.tournament_service.mapper.tournament.TournamentMapper;
import com.vkr.tournament_service.repository.tournament.TournamentRepository;
import com.vkr.tournament_service.service.tournament.TournamentServiceImpl;
import com.vkr.tournament_service.validator.tournament.TournamentValidator;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TournamentServiceTests {

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private TournamentMapper tournamentMapper;

    @Mock
    private TournamentValidator tournamentValidator;

    @InjectMocks
    private TournamentServiceImpl tournamentService;

    private Tournament tournament;
    private TournamentDto tournamentDto;
    private TournamentCreateDto tournamentCreateDto;

    @BeforeEach
    void setUp() {
        tournament = new Tournament();
        tournament.setId(UUID.randomUUID());
        tournament.setTournamentName("Test Tournament");
        tournament.setTournamentStatus(TournamentStatus.ACTIVE);
        tournament.setStages(List.of(Stage.fromName("Groups")));

        tournamentDto = new TournamentDto();
        tournamentDto.setTournamentName("Test Tournament");

        tournamentCreateDto = new TournamentCreateDto();
        tournamentCreateDto.setStages(List.of("Groups"));
    }

    @Test
    void getTournamentByName_ShouldReturnTournament() {
        when(tournamentRepository.findByTournamentName("Test Tournament"))
                .thenReturn(Optional.of(tournament));
        when(tournamentMapper.toDto(tournament)).thenReturn(tournamentDto);

        TournamentDto result = tournamentService.getTournamentByName("Test Tournament");

        assertNotNull(result);
        assertEquals("Test Tournament", result.getTournamentName());
        verify(tournamentRepository, times(1)).findByTournamentName("Test Tournament");
    }

    @Test
    void getTournamentByName_ShouldThrowException_WhenNotFound() {
        when(tournamentRepository.findByTournamentName("Unknown"))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> tournamentService.getTournamentByName("Unknown"));
    }

    @Test
    void createBaseTournament_ShouldSaveAndReturnTournament() {
        when(tournamentMapper.toEntity(any(TournamentCreateDto.class))).thenReturn(tournament);
        when(tournamentMapper.toDto(any(Tournament.class))).thenReturn(tournamentDto);
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);

        TournamentDto result = tournamentService.createBaseTournament(tournamentCreateDto);

        assertNotNull(result);
        assertEquals("Test Tournament", result.getTournamentName());
        verify(tournamentRepository, times(1)).save(any(Tournament.class));
    }

    @Test
    void updateTournament_ShouldUpdateAndReturnTournament() {
        TournamentUpdateDto updateDto = new TournamentUpdateDto();
        updateDto.setTournamentName("Updated Tournament");

        when(tournamentRepository.findByTournamentName("Test Tournament"))
                .thenReturn(Optional.of(tournament));
        doNothing().when(tournamentValidator).validateAccess(any(), any());
        when(tournamentMapper.updateEntity(eq(updateDto), any(Tournament.class))).thenReturn(tournament);
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);
        when(tournamentMapper.toDto(any(Tournament.class))).thenReturn(tournamentDto);

        TournamentDto result = tournamentService.updateTournament(updateDto, "Test Tournament");

        assertNotNull(result);
        verify(tournamentRepository, times(1)).save(any(Tournament.class));
    }

    @Test
    void deleteTournament_ShouldDeleteTournament() {
        when(tournamentRepository.findByTournamentName("Test Tournament"))
                .thenReturn(Optional.of(tournament));
        doNothing().when(tournamentValidator).validateAccess(any(), any());
        doNothing().when(tournamentRepository).delete(any(Tournament.class));

        tournamentService.deleteTournament("Test Tournament");

        verify(tournamentRepository, times(1)).delete(any(Tournament.class));
    }

    @Test
    void getAllTournaments_ShouldReturnPageOfTournaments() {
        Page<Tournament> tournamentPage = new PageImpl<>(List.of(tournament));
        when(tournamentRepository.findAll(any(Pageable.class))).thenReturn(tournamentPage);
        when(tournamentMapper.toDto(any(Tournament.class))).thenReturn(tournamentDto);

        Page<TournamentDto> result = tournamentService.getAllTournaments(Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(tournamentRepository, times(1)).findAll(any(Pageable.class));
    }
}

