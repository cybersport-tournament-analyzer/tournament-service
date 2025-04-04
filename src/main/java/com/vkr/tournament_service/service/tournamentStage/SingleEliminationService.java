package com.vkr.tournament_service.service.tournamentStage;

import com.vkr.tournament_service.entity.match.TournamentMatch;
import com.vkr.tournament_service.entity.schedule.ScheduleStatus;
import com.vkr.tournament_service.entity.schedule.TournamentSchedule;
import com.vkr.tournament_service.entity.team.TournamentTeam;
import com.vkr.tournament_service.entity.tournament.Tournament;
import com.vkr.tournament_service.entity.tournamentStage.TournamentStage;
import com.vkr.tournament_service.repository.match.MatchRepository;
import com.vkr.tournament_service.repository.team.TeamRepository;
import com.vkr.tournament_service.repository.tournamentStage.TournamentStageRepository;
import com.vkr.tournament_service.service.tournament.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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
        if (teams.size() < 2) {
            throw new IllegalArgumentException("Not enough teams to create a tournament bracket.");
        }

        teams.sort(Comparator.comparingInt(TournamentTeam::getAverageRating).reversed());

        for (int i = 0; i < teams.size(); i++) {
            teams.get(i).setSeed(i + 1);
        }

        teamRepository.saveAll(teams);

        int totalTeams = teams.size();
        int totalRounds = (int) Math.ceil(Math.log(totalTeams) / Math.log(2));

        TournamentStage stage = stageRepository.findAllByTournamentId(tournamentId).get(stageOrder);
        stage.setTotalRounds(totalRounds);
        stage.setCurrentRound(1);

        stage = stageRepository.save(stage);

        List<TournamentMatch> matches = new ArrayList<>();
        generateBracket(matches, teams, stage, tournament, 1, tournament.getTournamentStartTime(), new AtomicInteger(1), new HashMap<>());
//        generateBracket(matches, teams, stage, tournament, 1, tournament.getTournamentStartTime().plusDays(1), new AtomicInteger(1), new HashMap<>());
        stage.setMatches(matches);
        stageRepository.save(stage);
    }


    private void generateBracket(List<TournamentMatch> matches, List<TournamentTeam> teams, TournamentStage stage, Tournament tournament, int round,
                                 OffsetDateTime startTime, AtomicInteger matchCounter, Map<Integer, TournamentMatch> matchMap) {
        if (teams.size() < 2) return;

        // Определяем ближайшую степень двойки
        int totalTeams = nextPowerOfTwo(teams.size());
        int missingTeams = totalTeams - teams.size();

        // Добавляем `null` вместо byeTeam
        for (int i = 0; i < missingTeams; i++) {
            teams.add(null);
        }

        List<TournamentMatch> roundMatches = new ArrayList<>();
        List<TournamentTeam> nextRoundTeams = new ArrayList<>();

        OffsetDateTime matchTime = startTime; // Время начала раунда

        for (int i = 0; i < teams.size() / 2; i++) {
            TournamentTeam team1 = teams.get(i);
            TournamentTeam team2 = teams.get(teams.size() - 1 - i);

            int matchNumber = matchCounter.getAndIncrement(); // Уникальный номер матча

            // Создаём матч
            TournamentMatch match = TournamentMatch.builder()
                    .stage(stage)
                    .team1(team1)
                    .team2(team2)
                    .round(round)
                    .matchFormat("bo1")
                    .team1Score(0)
                    .team2Score(0)
                    .tournament(tournament)
                    .matchNumber(matchNumber)
                    .build();

            // Создаём расписание
            TournamentSchedule schedule = TournamentSchedule.builder()
                    .match(match)
                    .scheduledStartTime(matchTime)
                    .status(ScheduleStatus.SCHEDULED)
                    .build();

            match.setSchedule(schedule); // Привязываем расписание к матчу
            roundMatches.add(match);
            matchMap.put(matchNumber, match);

            int parentMatch1 = matchNumber - totalTeams / (int) Math.pow(2, round);
            int parentMatch2 = matchNumber - totalTeams / (int) Math.pow(2, round) + 1;

            TournamentMatch prevMatch1 = matchMap.get(parentMatch1);
            TournamentMatch prevMatch2 = matchMap.get(parentMatch2);

            boolean parent1Done = prevMatch1 == null || prevMatch1.getSchedule().getStatus() == ScheduleStatus.COMPLETED;
            boolean parent2Done = prevMatch2 == null || prevMatch2.getSchedule().getStatus() == ScheduleStatus.COMPLETED;
            boolean bothParentsDone = parent1Done && parent2Done;

            if (team1 == null && team2 == null) {
                if (bothParentsDone) {
                    schedule.setActualStartTime(startTime);
                    schedule.setActualEndTime(startTime);
                    schedule.setStatus(ScheduleStatus.COMPLETED);
                    match.setSchedule(schedule);
                    match.setWinnerTeamName("BYE");
                    match.setTeam1Score(1);
                }
                nextRoundTeams.add(null);
            } else if (team1 == null && bothParentsDone) {
                schedule.setActualStartTime(startTime);
                schedule.setActualEndTime(startTime);
                schedule.setStatus(ScheduleStatus.COMPLETED);
                match.setSchedule(schedule);
                match.setWinnerTeamName(team2.getTeamName());
                match.setTeam2Score(1);
                nextRoundTeams.add(team2);
            } else if (team2 == null && bothParentsDone) {
                schedule.setActualStartTime(startTime);
                schedule.setActualEndTime(startTime);
                schedule.setStatus(ScheduleStatus.COMPLETED);
                match.setSchedule(schedule);
                match.setWinnerTeamName(team1.getTeamName());
                match.setTeam1Score(1);
                nextRoundTeams.add(team1);
            } else {
                nextRoundTeams.add(null);
            }

            // Увеличиваем время начала следующего матча (например, каждый матч через 1 час)
            matchTime = matchTime.plusHours(1);
        }

        matches.addAll(roundMatches);

        // Запускаем следующий раунд через 1 день после последнего матча
        OffsetDateTime nextRoundStartTime = matchTime.plusDays(1);
        generateBracket(matches, nextRoundTeams, stage, tournament, round + 1, nextRoundStartTime, matchCounter, matchMap);
    }


    private int nextPowerOfTwo(int n) {
        int power = 1;
        while (power < n) {
            power *= 2;
        }
        return power;
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

    public List<Map<String, Object>> getBracket(UUID tournamentId) {
        List<TournamentMatch> matches = matchRepository.findAllByTournamentIdOrderByRoundAsc(tournamentId);
        List<Map<String, Object>> bracket = new ArrayList<>();

        for (TournamentMatch match : matches) {
            Map<String, Object> matchData = new HashMap<>();
            matchData.put("round", match.getRound());

            // Обработка team1
            matchData.put("team1", processTeamData(
                    match.getTeam1(),
                    match.getTeam1Score()
            ));

            // Обработка team2
            matchData.put("team2", processTeamData(
                    match.getTeam2(),
                    match.getTeam2Score()
            ));

            bracket.add(matchData);
        }

        return bracket;
    }


    private Map<String, Object> processTeamData(TournamentTeam team, int score) {
        Map<String, Object> teamData = new HashMap<>();

        if (team != null) {
            teamData.put("seed", team.getSeed());
            teamData.put("name", team.getTeamName());
            teamData.put("score", score);
        } else {
            teamData.put("seed", 0);
            teamData.put("name", null);
            teamData.put("score", 0);
        }

        return teamData;
    }

}

