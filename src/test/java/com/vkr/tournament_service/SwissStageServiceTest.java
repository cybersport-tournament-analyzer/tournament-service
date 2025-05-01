package com.vkr.tournament_service;

import com.vkr.tournament_service.entity.match.TournamentMatch;
import com.vkr.tournament_service.entity.team.TournamentTeam;
import com.vkr.tournament_service.entity.tournament.Tournament;
import com.vkr.tournament_service.entity.tournamentStage.Stage;
import com.vkr.tournament_service.entity.tournamentStage.TournamentStage;
import com.vkr.tournament_service.mapper.team.TeamMapper;
import com.vkr.tournament_service.repository.match.MatchRepository;
import com.vkr.tournament_service.repository.team.TeamRepository;
import com.vkr.tournament_service.repository.tournament.TournamentRepository;
import com.vkr.tournament_service.repository.tournamentStage.TournamentStageRepository;
import com.vkr.tournament_service.service.tournamentStage.SwissStageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SwissStageServiceTest {

    @InjectMocks
    private SwissStageService swissStageService;

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private TournamentStageRepository stageRepository;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamMapper teamMapper;

    private Tournament tournament;
    private TournamentStage stage;
    private List<TournamentTeam> teams;

    @BeforeEach
    public void setUp() {
        tournament = Tournament.builder()
                .id(UUID.randomUUID())
                .tournamentStartTime(OffsetDateTime.now())
                .build();

        stage = TournamentStage.builder()
                .id(UUID.randomUUID())
                .tournament(tournament)
                .stageType(Stage.SWISS)
                .matchFormat("BO1")
                .matches(new ArrayList<>())
                .build();

        teams = IntStream.range(1, 9)
                .mapToObj(i -> TournamentTeam.builder().id(UUID.randomUUID()).teamName("Team" + i).build())
                .collect(Collectors.toList());
    }

    @Test
    public void testSwissStage_AdvanceThrough3Rounds() {
        swissStageService.createStage(stage, teams);
        System.out.println(stage.getMatches().size());
        System.out.println(stage.getTotalRounds());

        List<TournamentMatch> round1 = stage.getMatches().stream()
                .filter(m -> m.getRound() == 1)
                .toList();

        when(teamRepository.findAllByTournamentId(stage.getTournament().getId())).thenReturn(teams);

        // Раунд 1: Побеждают team1 во всех матчах
        for (TournamentMatch match : round1) {
            match.setWinnerTeamName(match.getTeam1().getTeamName());
            match.setTeam1Score(1);
            match.setTeam2Score(0);
            match.setWinRoundsTeam1(13);
            match.setWinRoundsTeam2(5);
            match.setLoseRoundsTeam1(5);
            match.setLoseRoundsTeam2(13);
            swissStageService.advanceTeam(match);
        }


        stage.getMatches().stream().filter(match -> match.getRound() == 1).forEach(m ->
                System.out.println(m.getTeam1().getTeamName() + " " + m.getTeam2().getTeamName() + " " + m.getWinnerTeamName()));

        List<TournamentMatch> round2 = stage.getMatches().stream()
                .filter(m -> m.getRound() == 2)
                .filter(m -> m.getTeam1() != null && m.getTeam2() != null)
                .toList();


        // Раунд 2: 4 выигрыша team1 (идут в 2:0), 4 выигрыша team2 (идут в 1:1)
        for (int i = 0; i < round2.size(); i++) {
            TournamentMatch match = round2.get(i);
            match.setWinnerTeamName(match.getTeam1().getTeamName());
            match.setTeam1Score(1);
            match.setTeam2Score(0);
            match.setWinRoundsTeam1(13);
            match.setWinRoundsTeam2(5);
            match.setLoseRoundsTeam1(5);
            match.setLoseRoundsTeam2(13);
            swissStageService.advanceTeam(match);
        }

        stage.getMatches().stream().filter(match -> match.getRound() == 2).forEach(m ->
                System.out.println(m.getTeam1().getTeamName() + " " + m.getTeam2().getTeamName() + " " + m.getWinnerTeamName()));
        List<TournamentMatch> round3 = stage.getMatches().stream()
                .filter(m -> m.getRound() == 3)
                .filter(m -> m.getTeam1() != null && m.getTeam2() != null)
                .toList();

        for (int i = 0; i < round3.size(); i++) {
            TournamentMatch match = round3.get(i);
            match.setWinnerTeamName(match.getTeam1().getTeamName());
            match.setTeam1Score(1);
            match.setTeam2Score(0);
            match.setWinRoundsTeam1(13);
            match.setWinRoundsTeam2(5);
            match.setLoseRoundsTeam1(5);
            match.setLoseRoundsTeam2(13);
            swissStageService.advanceTeam(match);
        }
        stage.getMatches().stream().filter(match -> match.getRound() == 3).forEach(m ->
                System.out.println(m.getTeam1().getTeamName() + " " + m.getTeam2().getTeamName() + " " + m.getWinnerTeamName()));


//        List<TournamentMatch> round4 = stage.getMatches().stream()
//                .filter(m -> m.getRound() == 4)
//                .filter(m -> m.getTeam1() != null && m.getTeam2() != null)
//                .toList();
//
//        for (int i = 0; i < round4.size(); i++) {
//            TournamentMatch match = round4.get(i);
//            match.setWinnerTeamName(match.getTeam1().getTeamName());
//            match.setTeam1Score(1);
//            match.setTeam2Score(0);
//            match.setWinRoundsTeam1(13);
//            match.setWinRoundsTeam2(5);
//            match.setLoseRoundsTeam1(5);
//            match.setLoseRoundsTeam2(13);
//            swissStageService.advanceTeam(match);
//        }
//        stage.getMatches().stream().filter(match -> match.getRound() == 4).forEach(m ->
//                System.out.println(m.getTeam1().getTeamName() + " " + m.getTeam2().getTeamName() + " " + m.getWinnerTeamName()));
//
//        List<TournamentMatch> round5 = stage.getMatches().stream()
//                .filter(m -> m.getRound() == 5)
//                .filter(m -> m.getTeam1() != null && m.getTeam2() != null)
//                .toList();
//
//        for (int i = 0; i < round5.size(); i++) {
//            TournamentMatch match = round5.get(i);
//            match.setWinnerTeamName(match.getTeam1().getTeamName());
//            match.setTeam1Score(1);
//            match.setTeam2Score(0);
//            match.setWinRoundsTeam1(13);
//            match.setWinRoundsTeam2(5);
//            match.setLoseRoundsTeam1(5);
//            match.setLoseRoundsTeam2(13);
//            swissStageService.advanceTeam(match);
//        }
//        stage.getMatches().stream().filter(match -> match.getRound() == 5).forEach(m ->
//                System.out.println(m.getTeam1().getTeamName() + " " + m.getTeam2().getTeamName() + " " + m.getWinnerTeamName()));


        System.out.println("-----------------------------------------");
        stage.getMatches().forEach(m ->
                System.out.println(m.getTeam1().getTeamName() + " " + m.getTeam2().getTeamName() + " " + m.getWinnerTeamName()));

        System.out.println(swissStageService.getCurrentStandings(stage));
    }
}
