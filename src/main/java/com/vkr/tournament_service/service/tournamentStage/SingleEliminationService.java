package com.vkr.tournament_service.service.tournamentStage;

import com.vkr.tournament_service.entity.match.TournamentMatch;
import com.vkr.tournament_service.entity.team.TournamentTeam;
import com.vkr.tournament_service.entity.tournament.Tournament;
import com.vkr.tournament_service.entity.tournamentStage.Stage;
import com.vkr.tournament_service.entity.tournamentStage.TournamentStage;
import com.vkr.tournament_service.repository.match.MatchRepository;
import com.vkr.tournament_service.repository.team.TeamRepository;
import com.vkr.tournament_service.repository.tournamentStage.TournamentStageRepository;
import com.vkr.tournament_service.service.tournament.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SingleEliminationService {

    private final TournamentService tournamentService;
    private final TournamentStageRepository stageRepository;
    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;

    public void createSingleEliminationStage(UUID tournamentId, int stageOrder) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);

        List<TournamentTeam> teams = teamRepository.findAllByTournamentId(tournamentId);
        int totalTeams = teams.size();
        int totalRounds = (int) Math.ceil(Math.log(totalTeams) / Math.log(2));

        Collections.shuffle(teams);

        TournamentStage stage = TournamentStage.builder()
                .tournament(tournament)
                .stageOrder(stageOrder)
                .stageType(Stage.SINGLE_ELIMINATION)
                .totalRounds(totalRounds)
                .currentRound(1)
                .build();

        stage = stageRepository.save(stage);

        // Создаем первый раунд матчей
        List<TournamentMatch> matches = new ArrayList<>();
        for (int i = 0; i < totalTeams / 2; i++) {
            TournamentMatch match = TournamentMatch.builder()
                    .stage(stage)
                    .team1(teams.get(i))
                    .team2(teams.get(totalTeams - 1 - i))
                    .round(1)
                    .matchFormat("bo1")
                    .team1Score(0)
                    .team2Score(0)
                    .matchStatus("SOON")
                    .tournament(tournament)
                    .build();
            matches.add(match);
        }

        matchRepository.saveAll(matches);
        stage.setMatches(matches);
    }

    public void advanceToNextRound(TournamentStage stage) {
        if (stage.getCurrentRound() >= stage.getTotalRounds()) {
            throw new IllegalStateException("Tournament is already finished");
        }

        List<TournamentMatch> previousRoundMatches = stage.getMatches().stream()
                .filter(match -> match.getRound() == stage.getCurrentRound())
                .toList();

        List<TournamentTeam> winners = previousRoundMatches.stream()
                .map(tournamentMatch -> teamRepository.findByTeamNameAndTournamentId(tournamentMatch.getWinnerTeamName(),
                        stage.getTournament().getId()))
                .toList();

        int nextRound = stage.getCurrentRound() + 1;
        List<TournamentMatch> nextRoundMatches = new ArrayList<>();

        for (int i = 0; i < winners.size() / 2; i++) {
            TournamentMatch match = TournamentMatch.builder()
                    .stage(stage)
                    .team1(winners.get(i))
                    .team2(winners.get(winners.size() - 1 - i))
                    .round(nextRound)
                    .build();
            nextRoundMatches.add(match);
        }

        matchRepository.saveAll(nextRoundMatches);
        stage.getMatches().addAll(nextRoundMatches);
        stage.setCurrentRound(nextRound);
        stageRepository.save(stage);
    }
}

