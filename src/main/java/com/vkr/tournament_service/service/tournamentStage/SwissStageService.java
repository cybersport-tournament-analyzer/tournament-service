package com.vkr.tournament_service.service.tournamentStage;

import com.vkr.tournament_service.dto.team.TeamStandingsDto;
import com.vkr.tournament_service.entity.match.TournamentMatch;
import com.vkr.tournament_service.entity.schedule.ScheduleStatus;
import com.vkr.tournament_service.entity.schedule.TournamentSchedule;
import com.vkr.tournament_service.entity.team.TournamentTeam;
import com.vkr.tournament_service.entity.tournament.Tournament;
import com.vkr.tournament_service.entity.tournamentStage.Pair;
import com.vkr.tournament_service.entity.tournamentStage.Stage;
import com.vkr.tournament_service.entity.tournamentStage.TournamentStage;
import com.vkr.tournament_service.mapper.team.TeamMapper;
import com.vkr.tournament_service.repository.match.MatchRepository;
import com.vkr.tournament_service.repository.team.TeamRepository;
import com.vkr.tournament_service.repository.tournamentStage.TournamentStageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SwissStageService implements StageService {

    private final TournamentStageRepository stageRepository;
    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;

    @Override
    public Stage getStageType() {
        return Stage.SWISS;
    }

    @Override
    public void createStage(TournamentStage stage, List<TournamentTeam> teams) {
        Tournament tournament = stage.getTournament();
        stage.setTeamsToAdvance(teams.size() / 2);

        TournamentMatch lastMatchInPreviousStage = findLatestMatchFromPreviousStage(stage).orElse(null);
        OffsetDateTime startTime = lastMatchInPreviousStage != null ?
                lastMatchInPreviousStage.getSchedule().getScheduledStartTime().plusDays(1)
                : tournament.getTournamentStartTime().plusDays(1);

        if (!isPowerOfTwo(teams.size())) {
            throw new IllegalArgumentException("Teams number should be power of 2!");
        }

        int numberOfWinsToGo = teams.size() == 2 ? 1 : nextPowerOfTwo(teams.size()) - 1;
        int rounds = numberOfWinsToGo * 2 - 1;

        stage.setTotalRounds(rounds);
        stageRepository.save(stage);

        List<TournamentMatch> matches = new ArrayList<>();

        generateBracket(matches, teams, stage, tournament, startTime, numberOfWinsToGo);

        stage.getMatches().addAll(matches);
        stageRepository.save(stage);
    }

    private void generateBracket(List<TournamentMatch> matches, List<TournamentTeam> teams,
                                 TournamentStage stage, Tournament tournament,
                                 OffsetDateTime startTime,
                                 int winsToGo) {

        Comparator<Pair<Integer, Integer>> pairComparator = Comparator
                .comparing(Pair<Integer, Integer>::getFirst)
                .thenComparing(Pair::getSecond);

        Queue<Pair<Integer, Integer>> scores = new ArrayDeque<>();
        Map<Pair<Integer, Integer>, List<TournamentTeam>> rounds = new TreeMap<>(pairComparator);
        scores.add(Pair.of(0, 0));
        rounds.put(Pair.of(0, 0), teams);
        while (!scores.isEmpty()) {
            Pair<Integer, Integer> parent = scores.poll();
            Pair<Integer, Integer> child1 = Pair.of(parent.getFirst() + 1, parent.getSecond());
            Pair<Integer, Integer> child2 = Pair.of(parent.getFirst(), parent.getSecond() + 1);

            ArrayList<TournamentTeam> arr = new ArrayList<>(rounds.get(parent).size() / 2);
            {
                for (int i = 0; i < rounds.get(parent).size() / 2; i++) arr.add(null);
            }
            for (Pair<Integer, Integer> child : List.of(child1, child2)) {
                boolean containsChild = scores.stream().anyMatch(p -> p.equals(child));
                if (child.getFirst() < winsToGo && child.getSecond() < winsToGo) {
                    if (!containsChild) {
                        scores.add(child);
                        rounds.put(child, new ArrayList<>());
                    }
                    rounds.get(child).addAll(arr);
                }
            }
        }

        int matchNumber = 1;
        for (Pair<Integer, Integer> key : rounds.keySet()) {
            int round = key.getFirst() + key.getSecond() + 1;
            OffsetDateTime currentStartTime = startTime.plusDays(round - 1);
            List<TournamentTeam> teamsInRound = rounds.get(key);
            for (int i = 0; i < teamsInRound.size() / 2; i++) {
                TournamentTeam team1 = teamsInRound.get(i);
                TournamentTeam team2 = teamsInRound.get(teamsInRound.size() / 2 + i);
                String matchFormat = stage.getMatchFormat();

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

                TournamentSchedule schedule = TournamentSchedule.builder()
                        .match(match)
                        .scheduledStartTime(currentStartTime)
                        .status(ScheduleStatus.SCHEDULED)
                        .build();

                match.setSchedule(schedule);

                matches.add(match);
                currentStartTime = currentStartTime.plusHours(1);
                matchNumber += 1;
            }
        }
    }

    @Override
    public List<List<List<Map<String, Object>>>> getBracket(TournamentStage stage) {
        List<TournamentMatch> matches = stage.getMatches();

        int totalRounds = stage.getTotalRounds();
        List<List<List<Map<String, Object>>>> bracket = new ArrayList<>();

        for (int round = 1; round <= totalRounds; round++) {
            List<List<Map<String, Object>>> roundMatches = new ArrayList<>();
            for (TournamentMatch match : matches) {
                if (match.getRound() == round) {
                    List<Map<String, Object>> matchInfo = new ArrayList<>();

                    Map<String, Object> matchData = new HashMap<>();
                    matchData.put("matchId", match.getId());
                    matchData.put("team1", match.getTeam1() != null ? match.getTeam1().getTeamName() : null);
                    matchData.put("team2", match.getTeam2() != null ? match.getTeam2().getTeamName() : null);
                    matchData.put("team1Score", match.getTeam1Score());
                    matchData.put("team2Score", match.getTeam2Score());
                    matchData.put("winner", match.getWinnerTeamName());
                    matchData.put("round", match.getRound());

                    matchInfo.add(matchData);
                    roundMatches.add(matchInfo);
                }
            }
            bracket.add(roundMatches);
        }

        return bracket;
    }

    @Override
    public List<List<List<Map<String, Object>>>> updateBracket(List<List<List<Map<String, Object>>>> bracket, TournamentStage stage) {
        return List.of();
    }

    @Override
    public void advanceTeam(TournamentMatch match) {
        TournamentStage stage = match.getStage();
        List<TournamentMatch> allMatches = stage.getMatches();
        int currentRound = match.getRound();

        List<TournamentMatch> currentRoundMatches = allMatches.stream()
                .filter(m -> m.getRound() == currentRound)
                .toList();

        // Проверяем, завершились ли все матчи текущего раунда
        if (currentRoundMatches.stream().anyMatch(m -> m.getWinnerTeamName() == null)) return;

        int nextRound = currentRound + 1;
        List<TournamentMatch> nextRoundMatches = allMatches.stream()
                .filter(m -> m.getRound() == nextRound)
                .toList();


        if (nextRoundMatches.isEmpty()) return; // последний раунд

        List<TournamentTeam> allTeams = teamRepository.findAllByTournamentId(stage.getTournament().getId());
        Map<UUID, Integer> wins = new HashMap<>();
        Map<UUID, Integer> losses = new HashMap<>();
        Map<UUID, Integer> buchholz = new HashMap<>();
        Map<UUID, Integer> roundDiff = new HashMap<>();
        Map<UUID, Set<UUID>> pastOpponents = new HashMap<>();

        for (TournamentMatch m : allMatches) {
            TournamentTeam t1 = m.getTeam1();
            TournamentTeam t2 = m.getTeam2();
            if (t1 == null || t2 == null || m.getWinnerTeamName() == null) continue;

            UUID id1 = t1.getId();
            UUID id2 = t2.getId();

            int score1 = m.getWinRoundsTeam1();
            int score2 = m.getWinRoundsTeam2();

            pastOpponents.computeIfAbsent(id1, k -> new HashSet<>()).add(id2);
            pastOpponents.computeIfAbsent(id2, k -> new HashSet<>()).add(id1);

            if (m.getWinnerTeamName().equals(t1.getTeamName())) {
                wins.merge(id1, 1, Integer::sum);
                wins.putIfAbsent(id2, 0);
                losses.merge(id2, 1, Integer::sum);
                losses.putIfAbsent(id1, 0);
            } else {
                wins.merge(id2, 1, Integer::sum);
                wins.putIfAbsent(id1, 0);
                losses.merge(id1, 1, Integer::sum);
                losses.putIfAbsent(id2, 0);
            }

            roundDiff.merge(id1, score1 - score2, Integer::sum);
            roundDiff.merge(id2, score2 - score1, Integer::sum);
        }

        // Вычисляем Бухгольц
        for (TournamentTeam team : allTeams) {
            UUID tid = team.getId();
            int bSum = 0;
            for (UUID oppId : pastOpponents.getOrDefault(tid, Set.of())) {
                bSum += wins.getOrDefault(oppId, 0);
            }
            buchholz.put(tid, bSum);
        }

        int numberOfWinsToGo = (stage.getTotalRounds() + 1) / 2;

        // Исключаем команды, которые уже прошли или выбыли
        List<TournamentTeam> activeTeams = allTeams.stream()
                .filter(team -> {
                    int win = wins.getOrDefault(team.getId(), 0);
                    int loss = losses.getOrDefault(team.getId(), 0);
                    return win < numberOfWinsToGo && loss < numberOfWinsToGo;
                })
                .toList();

        // Сортировка
        List<TournamentTeam> sortedTeams = activeTeams.stream()
                .sorted(Comparator
                        .comparing((TournamentTeam t) -> wins.getOrDefault(t.getId(), 0)).reversed()
                        .thenComparing(t -> losses.getOrDefault(t.getId(), 0))
                        .thenComparing(t -> buchholz.getOrDefault(t.getId(), 0), Comparator.reverseOrder())
                        .thenComparing(t -> roundDiff.getOrDefault(t.getId(), 0), Comparator.reverseOrder())
                )
                .toList();

        // Группировка по числу побед
        Map<Integer, List<TournamentTeam>> groupedByWins = new TreeMap<>(Comparator.reverseOrder());
        for (TournamentTeam team : sortedTeams) {
            int w = wins.getOrDefault(team.getId(), 0);
            groupedByWins.computeIfAbsent(w, k -> new ArrayList<>()).add(team);
        }

        Set<String> usedPairs = allMatches.stream()
                .map(m -> pairKey(m.getTeam1(), m.getTeam2()))
                .collect(Collectors.toSet());

        List<TournamentMatch> unfilledMatches = new ArrayList<>(nextRoundMatches);

        for (List<TournamentTeam> group : groupedByWins.values()) {
            List<TournamentTeam> teams = new ArrayList<>(group);
            while (teams.size() >= 2 && !unfilledMatches.isEmpty()) {
                TournamentTeam t1 = teams.remove(0);
                TournamentTeam t2 = null;
                for (int i = 1; i < teams.size(); i++) {
                    TournamentTeam candidate = teams.get(i);
                    if (!usedPairs.contains(pairKey(t1, candidate))) {
                        t2 = candidate;
                        teams.remove(i);
                        break;
                    }
                }
                if (t2 == null) t2 = teams.get(0);
                teams.remove(t2);

                TournamentMatch m = unfilledMatches.remove(0);
                m.setTeam1(t1);
                m.setTeam2(t2);
                usedPairs.add(pairKey(t1, t2));
                matchRepository.save(m);
            }
        }
    }

    @Override
    public boolean isStageFinished(TournamentStage stage) {
        return stage.getMatches().stream().allMatch(match -> match.getWinnerTeamName() != null);
    }

    private String pairKey(TournamentTeam t1, TournamentTeam t2) {
        if (t1 == null || t2 == null) return "";
        UUID id1 = t1.getId();
        UUID id2 = t2.getId();
        return id1.compareTo(id2) < 0 ? id1 + "_" + id2 : id2 + "_" + id1;
    }

    @Override
    public List<TournamentMatch> findParentMatches(TournamentMatch match) {
        if (match.getRound() == 1) {
            return Collections.emptyList();
        }
        return matchRepository.findByStageAndRound(match.getStage(), match.getRound() - 1);
    }

    @Override
    public List<TournamentMatch> findChildMatches(TournamentMatch match) {
        if (match.getRound() >= match.getStage().getTotalRounds()) {
            return Collections.emptyList();
        }
        return matchRepository.findByStageAndRound(match.getStage(), match.getRound() + 1);
    }

    @Override
    public List<TeamStandingsDto> getCurrentStandings(TournamentStage stage) {
        List<TournamentMatch> matches = stage.getMatches();
        List<TournamentTeam> allTeams = teamRepository.findAllByTournamentId(stage.getTournament().getId());

        Map<UUID, Integer> wins = new HashMap<>();
        Map<UUID, Integer> losses = new HashMap<>();
        Map<UUID, Integer> roundsWon = new HashMap<>();
        Map<UUID, Integer> roundsLost = new HashMap<>();
        Map<UUID, Integer> buchholz = new HashMap<>();
        Map<UUID, Integer> roundDiff = new HashMap<>();
        Map<UUID, Set<UUID>> pastOpponents = new HashMap<>();

        for (TournamentMatch match : matches) {
            TournamentTeam t1 = match.getTeam1();
            TournamentTeam t2 = match.getTeam2();
            if (t1 == null || t2 == null || match.getWinnerTeamName() == null) continue;

            UUID id1 = t1.getId();
            UUID id2 = t2.getId();

            int score1 = match.getWinRoundsTeam1();
            int score2 = match.getWinRoundsTeam2();
            roundsWon.merge(id1, score1, Integer::sum);
            roundsLost.merge(id1, score2, Integer::sum);
            roundsWon.merge(id2, score2, Integer::sum);
            roundsLost.merge(id2, score1, Integer::sum);

            pastOpponents.computeIfAbsent(id1, k -> new HashSet<>()).add(id2);
            pastOpponents.computeIfAbsent(id2, k -> new HashSet<>()).add(id1);

            if (match.getWinnerTeamName().equals(t1.getTeamName())) {
                wins.merge(id1, 1, Integer::sum);
                wins.putIfAbsent(id2, 0);
                losses.merge(id2, 1, Integer::sum);
                losses.putIfAbsent(id1, 0);
            } else {
                wins.merge(id2, 1, Integer::sum);
                wins.putIfAbsent(id1, 0);
                losses.merge(id1, 1, Integer::sum);
                losses.putIfAbsent(id2, 0);
            }

            roundDiff.merge(id1, score1 - score2, Integer::sum);
            roundDiff.merge(id2, score2 - score1, Integer::sum);
        }

        for (TournamentTeam team : allTeams) {
            UUID tid = team.getId();
            int sum = 0;
            for (UUID oppId : pastOpponents.getOrDefault(tid, Set.of())) {
                sum += wins.getOrDefault(oppId, 0);
            }
            buchholz.put(tid, sum);
        }

        List<TeamStandingsDto> standings = allTeams.stream()
                .sorted(Comparator
                        .comparing((TournamentTeam t) -> wins.getOrDefault(t.getId(), 0)).reversed()
                        .thenComparing(t -> losses.getOrDefault(t.getId(), 0))
                        .thenComparing(t -> buchholz.getOrDefault(t.getId(), 0), Comparator.reverseOrder())
                        .thenComparing(t -> roundDiff.getOrDefault(t.getId(), 0), Comparator.reverseOrder())
                        .thenComparing(TournamentTeam::getSeed))
                .map(team -> {
                    UUID tid = team.getId();
                    int win = wins.getOrDefault(tid, 0);
                    int loss = losses.getOrDefault(tid, 0);
                    int diff = roundDiff.getOrDefault(tid, 0);
                    int rw = roundsWon.getOrDefault(tid, 0);
                    int rl = roundsLost.getOrDefault(tid, 0);

                    return TeamStandingsDto.builder()
                            .teamDto(teamMapper.toDto(team))
                            .place(0) // назначим позже
                            .wins(win)
                            .losses(loss)
                            .points(win) // в Swiss, обычно 1 победа = 1 очко
                            .groupLetter("SWISS")
                            .roundsWon(rw) // если хранишь — подставь
                            .roundsLost(rl)
                            .roundDifference(diff)
                            .build();
                }).toList();

        // Назначение мест
        for (int i = 0; i < standings.size(); i++) {
            standings.get(i).setPlace(i + 1);
        }

        return standings;
    }

    @Override
    public List<TournamentTeam> getTeamsToNextStage(TournamentStage stage) {
        List<TournamentTeam> advanceTeamToNextStage = new ArrayList<>();
        List<TeamStandingsDto> teamStandingsDtoList = getCurrentStandings(stage);
        for (int i = 0; i < stage.getTeamsToAdvance(); i++) {
            advanceTeamToNextStage.add(teamMapper.toEntity(teamStandingsDtoList.get(i).getTeamDto()));
        }
        return advanceTeamToNextStage;
    }


    private int nextPowerOfTwo(int n) {
        int power = 1;
        while (Math.pow(2, power) < n) {
            power += 1;
        }
        return power;
    }

    private boolean isPowerOfTwo(int number) {
        return (number > 0) && ((number & (number - 1)) == 0);
    }


    private Optional<TournamentMatch> findLatestMatchFromPreviousStage(TournamentStage currentStage) {
        Tournament tournament = currentStage.getTournament();
        int currentOrder = currentStage.getStageOrder();

        return tournament.getStages().stream()
                .filter(stage -> stage.getStageOrder() == currentOrder - 1)
                .findFirst()
                .flatMap(prevStage ->
                        prevStage.getMatches().stream()
                                .filter(match -> match.getSchedule().getScheduledStartTime() != null)
                                .max(Comparator.comparing(m -> m.getSchedule().getActualStartTime()))
                );
    }

}
