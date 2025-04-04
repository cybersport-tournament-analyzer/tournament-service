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
        stageRepository.save(stage);

        List<TournamentMatch> matches = new ArrayList<>();
        AtomicInteger matchCounter = new AtomicInteger(1);
        Map<Integer, TournamentMatch> matchMap = new HashMap<>();

        generateBracket(matches, teams, stage, tournament, 1, tournament.getTournamentStartTime().plusDays(1), matchCounter, matchMap);

        // Добавляем матч за 3-е место, если включен
        if (stage.isMatchForTheThirdPlace()) {
            generateThirdPlaceMatch(matches, stage, tournament, matchCounter, matchMap);
        }

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
            boolean isFinal = round == stage.getTotalRounds();
            String matchFormat = isFinal ? stage.getFinalMatchFormat() : stage.getMatchFormat();

            // Создаём матч
            TournamentMatch match = TournamentMatch.builder()
                    .stage(stage)
                    .team1(team1)
                    .team2(team2)
                    .round(round)
                    .matchFormat(matchFormat)
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
                    match.setTeam1Score(getMaxScore(matchFormat));
                }
                nextRoundTeams.add(null);
            } else if (team1 == null && bothParentsDone) {
                schedule.setActualStartTime(startTime);
                schedule.setActualEndTime(startTime);
                schedule.setStatus(ScheduleStatus.COMPLETED);
                match.setSchedule(schedule);
                match.setWinnerTeamName(team2.getTeamName());
                match.setTeam2Score(getMaxScore(matchFormat));
                nextRoundTeams.add(team2);
            } else if (team2 == null && bothParentsDone) {
                schedule.setActualStartTime(startTime);
                schedule.setActualEndTime(startTime);
                schedule.setStatus(ScheduleStatus.COMPLETED);
                match.setSchedule(schedule);
                match.setWinnerTeamName(team1.getTeamName());
                match.setTeam1Score(getMaxScore(matchFormat));
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

    private int getMaxScore(String matchFormat){
        return switch (matchFormat) {
            case "bo3" -> 2;
            case "bo5" -> 3;
            default -> 1;
        };
    }

    private void generateThirdPlaceMatch(List<TournamentMatch> matches, TournamentStage stage,
                                         Tournament tournament, AtomicInteger matchCounter,
                                         Map<Integer, TournamentMatch> matchMap) {
        List<TournamentMatch> semiFinals = matches.stream()
                .filter(m -> m.getRound() == stage.getTotalRounds() - 1)
                .toList();

        if (semiFinals.size() < 2) return; // Должно быть два полуфинала

        TournamentTeam loser1 = getLoser(semiFinals.get(0));
        TournamentTeam loser2 = getLoser(semiFinals.get(1));


        int matchNumber = matchCounter.getAndIncrement();
        String matchFormat = stage.getFinalMatchFormat(); // Можно задать отдельный формат

        TournamentMatch thirdPlaceMatch = TournamentMatch.builder()
                .stage(stage)
                .team1(loser1)
                .team2(loser2)
                .round(stage.getTotalRounds())
                .matchNumber(matchNumber)
                .matchFormat(matchFormat)
                .team1Score(0)
                .team2Score(0)
                .tournament(tournament)
                .build();

        TournamentSchedule schedule = TournamentSchedule.builder()
                .match(thirdPlaceMatch)
                .scheduledStartTime(semiFinals.get(0).getSchedule().getScheduledStartTime().plusDays(1).minusHours(3))
                .status(ScheduleStatus.SCHEDULED)
                .build();

        thirdPlaceMatch.setSchedule(schedule);
        matches.add(thirdPlaceMatch);
        matchMap.put(matchNumber, thirdPlaceMatch);
    }

    private TournamentTeam getLoser(TournamentMatch match) {
        if (match.getWinnerTeamName() == null) return null;
        return match.getTeam1().getTeamName().equals(match.getWinnerTeamName()) ? match.getTeam2() : match.getTeam1();
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

//    private void advanceTeam(TournamentMatch match) {
//        TournamentStage stage = match.getStage();
//        int matchNumber = match.getMatchNumber();
//        int round = match.getRound();
//        int totalTeams = stage.getTotalTeams();
//
//        // Определим матч-ребёнка
//        int childRound = round + 1;
//        int childMatchNumber = totalTeams / (int) Math.pow(2, childRound) + (matchNumber - 1) / 2 + 1;
//
//        Optional<TournamentMatch> childOpt = Optional.ofNullable(matchRepository.findByStageAndRoundAndMatchNumber(stage, childRound, childMatchNumber));
//
//        TournamentMatch childMatch = childOpt.orElseGet(() -> {
//            TournamentMatch m = TournamentMatch.builder()
//                    .stage(stage)
//                    .round(childRound)
//                    .matchNumber(childMatchNumber)
//                    .matchFormat(childRound == stage.getTotalRounds() ? stage.getFinalMatchFormat() : stage.getMatchFormat())
//                    .tournament(stage.getTournament())
//                    .build();
//
//            TournamentSchedule schedule = TournamentSchedule.builder()
//                    .match(m)
//                    .scheduledStartTime(match.getSchedule().getScheduledStartTime().plusDays(1))
//                    .status(ScheduleStatus.SCHEDULED)
//                    .build();
//
//            m.setSchedule(schedule);
//            return m;
//        });
//
//        if (childMatch.getTeam1() == null) {
//            childMatch.setTeam1(winner);
//        } else if (childMatch.getTeam2() == null) {
//            childMatch.setTeam2(winner);
//        }
//
//        matchRepository.save(childMatch);
//    }
}

