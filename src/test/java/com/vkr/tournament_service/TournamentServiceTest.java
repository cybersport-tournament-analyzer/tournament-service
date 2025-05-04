package com.vkr.tournament_service;

import com.vkr.tournament_service.dto.team.TeamDto;
import com.vkr.tournament_service.dto.team.TeamStandingsDto;
import com.vkr.tournament_service.entity.tournament.Tournament;
import com.vkr.tournament_service.entity.tournamentStage.TournamentStage;
import com.vkr.tournament_service.repository.tournament.TournamentRepository;
import com.vkr.tournament_service.service.tournament.TournamentServiceImpl;
import com.vkr.tournament_service.service.tournamentStage.TournamentStageManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class TournamentServiceTest {

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private TournamentStageManager tournamentStageManager;

    @InjectMocks
    private TournamentServiceImpl tournamentService; // Класс, где находится getOverallStandings

    @Test
    void testGetOverallStandings_SortsByFinalStageResults() {
        UUID tournamentId = UUID.randomUUID();
        UUID team1 = UUID.randomUUID();
        UUID team2 = UUID.randomUUID();
        UUID team3 = UUID.randomUUID();
        UUID team4 = UUID.randomUUID();

        TournamentStage groupStage = new TournamentStage();
        groupStage.setId(UUID.randomUUID());
        groupStage.setStageOrder(1);

        TournamentStage finalStage = new TournamentStage();
        finalStage.setId(UUID.randomUUID());
        finalStage.setStageOrder(2);

        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setStages(List.of(groupStage, finalStage));

        TeamDto teamDto1 = TeamDto.builder()
                .id(team1)
                .teamName("Team A").build();
        TeamDto teamDto2 = TeamDto.builder()
                .id(team2)
                .teamName("Team B").build();
        TeamDto teamDto3 = TeamDto.builder()
                .id(team3)
                .teamName("Team C").build();
        TeamDto teamDto4 = TeamDto.builder()
                .id(team4)
                .teamName("Team D").build();

        TeamStandingsDto groupStanding1 = TeamStandingsDto.builder()
                .teamDto(teamDto1)
                .place(1)
                .wins(3)
                .points(10)
                .build();
        TeamStandingsDto groupStanding2 = TeamStandingsDto.builder()
                .teamDto(teamDto2)
                .place(1)
                .wins(4)
                .points(12)
                .build();
        TeamStandingsDto groupStanding3 = TeamStandingsDto.builder()
                .teamDto(teamDto3)
                .place(2)
                .wins(2)
                .points(7)
                .build();
        TeamStandingsDto groupStanding4 = TeamStandingsDto.builder()
                .teamDto(teamDto4)
                .place(2)
                .wins(2)
                .points(6)
                .build();

        TeamStandingsDto finalStanding1 = TeamStandingsDto.builder()
                .teamDto(teamDto1)
                .place(1)
                .wins(5)
                .points(15)
                .build();

        TeamStandingsDto finalStanding2 = TeamStandingsDto.builder()
                .teamDto(teamDto2)
                .place(2)
                .wins(4)
                .points(13)
                .build();

        Mockito.when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        Mockito.when(tournamentStageManager.getCurrentStandings(groupStage.getId()))
                .thenReturn(List.of(groupStanding1, groupStanding2, groupStanding3, groupStanding4));
        Mockito.when(tournamentStageManager.getCurrentStandings(finalStage.getId()))
                .thenReturn(List.of(finalStanding1, finalStanding2));

        List<TeamStandingsDto> standings = tournamentService.getOverallStandings(tournamentId.toString());

        // Проверка порядка: team1 (1 место на финальной стадии), team2 (2 место)
        assertEquals(4, standings.size());
        assertEquals(team1, standings.get(0).getTeamDto().getId());
        assertEquals(team2, standings.get(1).getTeamDto().getId());

        assertEquals(1, standings.get(0).getPlace());
        assertEquals(2, standings.get(1).getPlace());
        assertEquals(2, standings.get(2).getPlace());
        assertEquals(2, standings.get(3).getPlace());
    }
}

