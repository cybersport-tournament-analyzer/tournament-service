package com.vkr.tournament_service;

import com.vkr.tournament_service.dto.team.TeamDto;
import com.vkr.tournament_service.dto.team.TeamStandingsDto;
import com.vkr.tournament_service.entity.match.TournamentMatch;
import com.vkr.tournament_service.entity.team.TournamentTeam;
import com.vkr.tournament_service.entity.tournamentStage.TournamentStage;
import com.vkr.tournament_service.mapper.team.TeamMapper;
import com.vkr.tournament_service.service.tournamentStage.GroupsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupStageStandingsTest {

    @InjectMocks
    private GroupsService groupsService;

    @Mock
    private TeamMapper teamMapper;

    @Test
    void testThreeWayTieResolvedByRoundDifference() {
        TournamentTeam teamA = TournamentTeam.builder().teamName("A").build();
        TournamentTeam teamB = TournamentTeam.builder().teamName("B").build();
        TournamentTeam teamC = TournamentTeam.builder().teamName("C").build();
        TournamentTeam teamD = TournamentTeam.builder().teamName("D").build();

        List<TournamentMatch> matches = new ArrayList<>(List.of(
                createMatch("A", teamA, teamB, 13, 6),
                createMatch("A", teamB, teamC, 13, 4),
                createMatch("A", teamC, teamA, 13, 11),
                createMatch("A", teamA, teamD, 16, 13),
                createMatch("A", teamB, teamD, 13, 6),
                createMatch("A", teamD, teamC, 8, 13)
        ));


        TournamentStage stage = new TournamentStage();
        stage.setMatches(matches);

        when(teamMapper.toDto(any())).thenAnswer(invocation -> {
            TournamentTeam team = invocation.getArgument(0);
            return TeamDto.builder().teamName(team.getTeamName()).build();
        });

        List<TeamStandingsDto> standings = groupsService.getCurrentStandings(stage);

        assertEquals(4, standings.size());
        assertEquals("B", standings.get(0).getTeamDto().getTeamName()); // +4
        assertEquals("A", standings.get(1).getTeamDto().getTeamName()); // 0
        assertEquals("C", standings.get(2).getTeamDto().getTeamName());
        assertEquals("D", standings.get(3).getTeamDto().getTeamName());// -4
    }

    private TournamentMatch createMatch(String group, TournamentTeam t1, TournamentTeam t2,
                                        int winRoundsTeam1, int winRoundsTeam2) {
        TournamentMatch match = new TournamentMatch();
        match.setGroupLetter(group);
        match.setTeam1(t1);
        match.setTeam2(t2);
        match.setWinRoundsTeam1(winRoundsTeam1);
        match.setWinRoundsTeam2(winRoundsTeam2);
        if (winRoundsTeam1 > winRoundsTeam2) {
            match.setWinnerTeamName(t1.getTeamName());
        } else {
            match.setWinnerTeamName(t2.getTeamName());
        }
        return match;
    }
}

