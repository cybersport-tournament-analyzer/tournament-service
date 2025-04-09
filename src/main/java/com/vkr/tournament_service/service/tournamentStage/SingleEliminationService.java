package com.vkr.tournament_service.service.tournamentStage;

import com.vkr.tournament_service.dto.team.TeamStandingsDto;
import com.vkr.tournament_service.entity.match.TournamentMatch;
import com.vkr.tournament_service.entity.schedule.ScheduleStatus;
import com.vkr.tournament_service.entity.schedule.TournamentSchedule;
import com.vkr.tournament_service.entity.team.TournamentTeam;
import com.vkr.tournament_service.entity.tournament.Tournament;
import com.vkr.tournament_service.entity.tournamentStage.TournamentStage;
import com.vkr.tournament_service.exception.EntityNotFoundException;
import com.vkr.tournament_service.mapper.team.TeamMapper;
import com.vkr.tournament_service.repository.match.MatchRepository;
import com.vkr.tournament_service.repository.team.TeamRepository;
import com.vkr.tournament_service.repository.tournament.TournamentRepository;
import com.vkr.tournament_service.repository.tournamentStage.TournamentStageRepository;
import com.vkr.tournament_service.validator.tournament.TournamentValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SingleEliminationService {

    private final TournamentRepository tournamentRepository;
    private final TournamentValidator tournamentValidator;
    private final TournamentStageRepository stageRepository;
    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;

    public void createSingleEliminationStage(UUID tournamentId, int stageOrder) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new EntityNotFoundException("Tournament with id: " + tournamentId + " not found!"));

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
        tournament.setTeamsCount((long) totalTeams);
        tournamentRepository.save(tournament);

        int totalRounds = (int) Math.ceil(Math.log(totalTeams) / Math.log(2));

        TournamentStage stage = stageRepository.findAllByTournamentId(tournamentId).get(stageOrder);
        if (teams.size() < 4 && stage.isMatchForTheThirdPlace()) {
            stage.setMatchForTheThirdPlace(false);
        }
        stage.setTotalRounds(totalRounds);
        stageRepository.save(stage);

        List<TournamentMatch> matches = new ArrayList<>();
        AtomicInteger matchCounter = new AtomicInteger(1);
        Map<Integer, TournamentMatch> matchMap = new HashMap<>();

        generateBracket(matches, teams, stage, tournament, 1, tournament.getTournamentStartTime().plusDays(1), matchCounter, matchMap, false);

        // Добавляем матч за 3-е место, если включен
        if (stage.isMatchForTheThirdPlace()) {
            generateThirdPlaceMatch(matches, stage, tournament, matchCounter, matchMap);
        }

        stage.setMatches(matches);
        stageRepository.save(stage);
    }


    private void generateBracket(List<TournamentMatch> matches, List<TournamentTeam> teams, TournamentStage stage, Tournament tournament, int round,
                                 OffsetDateTime startTime, AtomicInteger matchCounter, Map<Integer, TournamentMatch> matchMap, boolean change) {
        if (teams.size() < 2) return;

        // Определяем ближайшую степень двойки
        int totalTeams = nextPowerOfTwo(Math.toIntExact(tournament.getTeamsCount()));

        if (round == 1 && !change) {
            int missingTeams = totalTeams - teams.size();
            for (int i = 0; i < missingTeams; i++) {
                teams.add(null);
            }

            // Генерируем олимпийский посев
            List<Integer> seedOrder = generateSeedOrder(totalTeams);
            List<TournamentTeam> orderedTeams = new ArrayList<>();
            for (int seed : seedOrder) {
                orderedTeams.add(teams.get(seed - 1));
            }
            teams = orderedTeams;
        }

        List<TournamentMatch> roundMatches = new ArrayList<>();
        List<TournamentTeam> nextRoundTeams = new ArrayList<>();

        OffsetDateTime matchTime = startTime; // Время начала раунда

        for (int i = 0; i < teams.size() / 2; i++) {
            TournamentTeam team1;
            TournamentTeam team2;

            team1 = teams.get(i * 2);
            team2 = teams.get(i * 2 + 1);

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

            TournamentMatch prevMatch1;
            TournamentMatch prevMatch2;


            if (round <= 1) {
                prevMatch1 = null;
                prevMatch2 = null;
            } else {
                int sumPreviousRounds = totalTeams - (totalTeams / (int) Math.pow(2, round - 1));
                int firstMatchInCurrentRound = sumPreviousRounds + 1;

                int sumPreviousPreviousRounds = totalTeams - (totalTeams / (int) Math.pow(2, round - 2));
                int firstMatchInPreviousRound = sumPreviousPreviousRounds + 1;

                int parentPairIndex = matchNumber - firstMatchInCurrentRound;
                int firstParentMatchNumber = parentPairIndex * 2 + firstMatchInPreviousRound;
                int secondParentMatchNumber = firstParentMatchNumber + 1;

                prevMatch1 = matchMap.get(firstParentMatchNumber);
                prevMatch2 = matchMap.get(secondParentMatchNumber);
            }

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
        generateBracket(matches, nextRoundTeams, stage, tournament, round + 1, nextRoundStartTime, matchCounter, matchMap, change);
    }

    private List<Integer> generateSeedOrder(int size) {
        List<Integer> seeds = new ArrayList<>();
        seeds.add(1);
        while (seeds.size() < size) {
            List<Integer> next = new ArrayList<>();
            int max = seeds.size() * 2 + 1;
            for (Integer seed : seeds) {
                next.add(seed);
                next.add(max - seed);
            }
            seeds = next;
        }
        return seeds;
    }


    private int nextPowerOfTwo(int n) {
        int power = 1;
        while (power < n) {
            power *= 2;
        }
        return power;
    }

    private int getMaxScore(String matchFormat) {
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

    private TournamentTeam getWinner(TournamentMatch match) {
        if (match.getWinnerTeamName() == null) return null;
        return match.getTeam1().getTeamName().equals(match.getWinnerTeamName()) ? match.getTeam1() : match.getTeam2();
    }


    public List<List<List<Map<String, Object>>>> getBracket(UUID tournamentId) {
        List<TournamentMatch> matches = matchRepository.findAllByTournamentIdOrderByRoundAsc(tournamentId);
        List<List<List<Map<String, Object>>>> bracket = new ArrayList<>();

        Integer currentRound = null;
        List<List<Map<String, Object>>> currentRoundMatches = null;

        for (TournamentMatch match : matches) {
            int round = match.getRound();

            if (currentRound == null || round != currentRound) {
                currentRound = round;
                currentRoundMatches = new ArrayList<>();
                bracket.add(currentRoundMatches);
            }

            List<Map<String, Object>> matchData = new ArrayList<>();
            matchData.add(processTeamData(match.getTeam1(), match.getTeam1Score()));
            matchData.add(processTeamData(match.getTeam2(), match.getTeam2Score()));

            currentRoundMatches.add(matchData);
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

    public void advanceTeam(TournamentMatch match) {
        TournamentStage stage = match.getStage();
        int matchNumber = match.getMatchNumber();
        int round = match.getRound();
        int totalTeams = nextPowerOfTwo(Math.toIntExact(stage.getTournament().getTeamsCount()));

        if (round == stage.getTotalRounds()) {
            return;
        }

        // Определим матч-ребёнка
        int childRound = round + 1;
        int startOfCurrentRound = totalTeams - (totalTeams / (int) Math.pow(2, round - 1)) + 1;
        int startOfNextRound = startOfCurrentRound + (totalTeams / (int) Math.pow(2, round));
        int childMatchNumber = startOfNextRound + (matchNumber - startOfCurrentRound) / 2;

        TournamentMatch childMatch = matchRepository.findByStageAndRoundAndMatchNumber(stage, childRound, childMatchNumber);

        TournamentTeam winner = match.getWinnerTeamName().equals(match.getTeam1().getTeamName()) ? match.getTeam1() : match.getTeam2();
        TournamentTeam loser = match.getWinnerTeamName().equals(match.getTeam1().getTeamName()) ? match.getTeam2() : match.getTeam1();


        if ((matchNumber - startOfCurrentRound) % 2 == 0) {
            childMatch.setTeam1(winner);
        } else {
            childMatch.setTeam2(winner);
        }
        matchRepository.save(childMatch);

        if (round == stage.getTotalRounds() - 1 && stage.isMatchForTheThirdPlace()) {
            int thirdPlaceMatchNumber = childMatchNumber + 1;
            TournamentMatch thirdPlaceMatch = matchRepository.findByStageAndRoundAndMatchNumber(
                    stage,
                    childRound,
                    thirdPlaceMatchNumber
            );
            if (matchNumber % 2 == 1) {
                thirdPlaceMatch.setTeam1(loser);
            } else {
                thirdPlaceMatch.setTeam2(loser);
            }
            matchRepository.save(thirdPlaceMatch);
        }
    }

    public List<TournamentMatch> findParentMatches(TournamentMatch match) {
        int round = match.getRound();
        TournamentStage stage = match.getStage();
        int matchNumber = match.getMatchNumber();

        if (round <= 1) return Collections.emptyList(); // Первый раунд — родителей нет

        int totalTeams = nextPowerOfTwo(Math.toIntExact(stage.getTournament().getTeamsCount()));
        int parentRound = round - 1;

        if (matchNumber == totalTeams) { // Проверка если это матч за 3-е место
            matchNumber -= 1;
        }

        int sumPreviousRounds = totalTeams - (totalTeams / (int) Math.pow(2, round - 1));
        int firstMatchInCurrentRound = sumPreviousRounds + 1;

        int sumPreviousPreviousRounds = totalTeams - (totalTeams / (int) Math.pow(2, round - 2));
        int firstMatchInPreviousRound = sumPreviousPreviousRounds + 1;

        int parentPairIndex = matchNumber - firstMatchInCurrentRound;
        int firstParentMatchNumber = parentPairIndex * 2 + firstMatchInPreviousRound;
        int secondParentMatchNumber = firstParentMatchNumber + 1;

        TournamentMatch parent1 = matchRepository.findByStageAndRoundAndMatchNumber(stage, parentRound, firstParentMatchNumber);
        TournamentMatch parent2 = matchRepository.findByStageAndRoundAndMatchNumber(stage, parentRound, secondParentMatchNumber);

        return Stream.of(parent1, parent2).filter(Objects::nonNull).toList();
    }

    public List<TournamentMatch> findChildMatches(TournamentMatch match) {
        TournamentStage stage = match.getStage();
        int currentRound = match.getRound();
        int matchNumber = match.getMatchNumber();
        int totalTeams = nextPowerOfTwo(Math.toIntExact(stage.getTournament().getTeamsCount()));

        List<TournamentMatch> children = new ArrayList<>();

        // Если это последний раунд - детей нет
        if (currentRound >= stage.getTotalRounds()) {
            return Collections.emptyList();
        }

        int childRound = currentRound + 1;
        int startOfCurrentRound = totalTeams - (totalTeams / (int) Math.pow(2, currentRound - 1)) + 1;
        int startOfNextRound = startOfCurrentRound + (totalTeams / (int) Math.pow(2, currentRound));
        int childMatchNumber = startOfNextRound + (matchNumber - startOfCurrentRound) / 2;

        TournamentMatch mainChild = matchRepository.findByStageAndRoundAndMatchNumber(
                stage,
                childRound,
                childMatchNumber
        );

        if (mainChild != null) {
            children.add(mainChild);
        }

        if (currentRound == stage.getTotalRounds() - 1 && stage.isMatchForTheThirdPlace()) {
            int thirdPlaceMatchNumber = childMatchNumber + 1;
            TournamentMatch thirdPlaceMatch = matchRepository.findByStageAndRoundAndMatchNumber(
                    stage,
                    childRound,
                    thirdPlaceMatchNumber
            );
            if (thirdPlaceMatch != null) {
                children.add(thirdPlaceMatch);
            }
        }
        return children;
    }

    @Transactional
    public List<TeamStandingsDto> getCurrentStandings(UUID tournamentId) {
        List<TournamentMatch> matches = matchRepository.findAllByTournamentIdOrderByRoundAsc(tournamentId);
        TournamentStage stage = Objects.requireNonNull(tournamentRepository.findById(tournamentId).orElse(null)).getStages().get(0);

        int totalTeams = nextPowerOfTwo(Math.toIntExact(stage.getTournament().getTeamsCount()));
        List<TeamStandingsDto> standings = new ArrayList<>();
        for (TournamentMatch match : matches) {
            if (match.getSchedule().getStatus().equals(ScheduleStatus.COMPLETED) && getLoser(match) != null) {
                if (match.getRound() == stage.getTotalRounds() && match.getMatchNumber() == totalTeams - 1) {
                    TeamStandingsDto secondPlace = TeamStandingsDto.builder().
                            teamDto(teamMapper.toDto(getLoser(match))).place(2).build();
                    standings.add(secondPlace);
                    TeamStandingsDto winner = TeamStandingsDto.builder().
                            teamDto(teamMapper.toDto(getWinner(match))).place(1).build();
                    standings.add(winner);
                } else if (match.getRound() == stage.getTotalRounds() && stage.isMatchForTheThirdPlace() && match.getMatchNumber() == totalTeams) {
                    TeamStandingsDto fourthPlace = TeamStandingsDto.builder().
                            teamDto(teamMapper.toDto(getLoser(match))).place(4).build();
                    standings.add(fourthPlace);
                    TeamStandingsDto thirdPlace = TeamStandingsDto.builder().
                            teamDto(teamMapper.toDto(getWinner(match))).place(3).build();
                    standings.add(thirdPlace);
                } else if (match.getRound() == stage.getTotalRounds() - 1 && stage.isMatchForTheThirdPlace()) {
                } else {
                    TeamStandingsDto teamStandingsDto = TeamStandingsDto.builder().
                            teamDto(teamMapper.toDto(getLoser(match))).place((int) (totalTeams / Math.pow(2, match.getRound()) + 1)).build();
                    standings.add(teamStandingsDto);
                }
            }
        }
        return standings;
    }

    @Transactional
    public List<List<List<Map<String, Object>>>> updateBracket(List<List<List<Map<String, Object>>>> bracket, String tournamentId, String userId) {
        Tournament tournament = tournamentRepository.findById(UUID.fromString(tournamentId))
                .orElseThrow(() -> new EntityNotFoundException("Tournament with id: " + tournamentId + " not found!"));
        TournamentStage stage = stageRepository.findAllByTournamentId(UUID.fromString(tournamentId)).get(0);
        tournamentValidator.validateAccess(UUID.fromString(tournamentId), userId);
        List<String> teamNames = new ArrayList<>();
        List<Map<String, Object>> firstRound = bracket.get(0).get(0);
        for (Map<String, Object> match : firstRound) {
            teamNames.add((String) match.get("name"));
        }
        List<TournamentTeam> teams = teamRepository.findByTournamentIdAndTeamNameIn(UUID.fromString(tournamentId), teamNames);

        List<TournamentMatch> matches = new ArrayList<>();
        AtomicInteger matchCounter = new AtomicInteger(1);
        Map<Integer, TournamentMatch> matchMap = new HashMap<>();

        generateBracket(matches, teams, stage, tournament, 1, tournament.getTournamentStartTime().plusDays(1), matchCounter, matchMap, true);
        if (stage.isMatchForTheThirdPlace()) {
            generateThirdPlaceMatch(matches, stage, tournament, matchCounter, matchMap);
        }

        stage.setMatches(matches);
        stageRepository.save(stage);

        return getBracket(UUID.fromString(tournamentId));
    }
}

