package com.vkr.tournament_service.service.tournamentStage;

import com.vkr.tournament_service.dto.team.TeamDto;
import com.vkr.tournament_service.dto.team.TeamStandingsDto;
import com.vkr.tournament_service.dto.team.TeamStats;
import com.vkr.tournament_service.entity.match.TournamentMatch;
import com.vkr.tournament_service.entity.schedule.ScheduleStatus;
import com.vkr.tournament_service.entity.schedule.TournamentSchedule;
import com.vkr.tournament_service.entity.team.TournamentTeam;
import com.vkr.tournament_service.entity.tournamentStage.Pair;
import com.vkr.tournament_service.entity.tournamentStage.Stage;
import com.vkr.tournament_service.entity.tournamentStage.TournamentStage;
import com.vkr.tournament_service.exception.WrongTournamentStatusException;
import com.vkr.tournament_service.mapper.team.TeamMapper;
import com.vkr.tournament_service.repository.tournamentStage.TournamentStageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GroupsService implements StageService {

    private final TournamentStageRepository stageRepository;
    private final TeamMapper teamMapper;

    @Override
    public Stage getStageType() {
        return Stage.GROUPS;
    }

    @Override
    public void createStage(TournamentStage stage, List<TournamentTeam> teams) {
        int numGroups = stage.getNumberOfGroups();
        int roundsPerPair = stage.getTotalRounds();

        List<List<TournamentTeam>> groups = splitIntoGroups(teams, numGroups);

        for (int groupIndex = 0; groupIndex < groups.size(); groupIndex++) {
            List<TournamentTeam> group = groups.get(groupIndex);
            String groupLetter = String.valueOf((char) ('A' + groupIndex));

            if (group.size() < 2) {
                throw new WrongTournamentStatusException("Group " + groupLetter + " has less than two teams.");
            }

            List<List<Pair<TournamentTeam, TournamentTeam>>> baseRounds = generateRoundRobinRounds(group);

            int matchNumber = 1;
            int roundNumber = 1;

            for (int r = 0; r < roundsPerPair; r++) {
                boolean reverse = r % 2 == 1;

                for (List<Pair<TournamentTeam, TournamentTeam>> baseRound : baseRounds) {
                    for (Pair<TournamentTeam, TournamentTeam> pair : baseRound) {
                        TournamentTeam team1 = reverse ? pair.getSecond() : pair.getFirst();
                        TournamentTeam team2 = reverse ? pair.getFirst() : pair.getSecond();

                        TournamentMatch match = TournamentMatch.builder()
                                .tournament(stage.getTournament())
                                .stage(stage)
                                .team1(team1)
                                .team2(team2)
                                .matchFormat(stage.getMatchFormat())
                                .round(roundNumber)
                                .matchNumber(matchNumber++)
                                .groupLetter(groupLetter)
                                .build();

                        TournamentSchedule schedule = TournamentSchedule.builder()
                                .match(match)
                                .status(ScheduleStatus.SCHEDULED)
                                .scheduledStartTime(stage.getTournament().getTournamentStartTime()
                                        .plusDays(match.getRound()))
                                .build();

                        match.setSchedule(schedule);
                        stage.getMatches().add(match);
                    }
                    roundNumber++;
                }
            }
        }

        stageRepository.save(stage);
    }

    private List<List<TournamentTeam>> splitIntoGroups(List<TournamentTeam> teams, int numGroups) {
        List<List<TournamentTeam>> groups = new ArrayList<>();
        for (int i = 0; i < numGroups; i++) {
            groups.add(new ArrayList<>());
        }

        for (int i = 0; i < teams.size(); i++) {
            groups.get(i % numGroups).add(teams.get(i));
        }

        return groups;
    }

    private List<List<Pair<TournamentTeam, TournamentTeam>>> generateRoundRobinRounds(List<TournamentTeam> teams) {
        List<TournamentTeam> teamList = new ArrayList<>(teams);
        if (teamList.size() % 2 != 0) {
            teamList.add(null); // Призрак/бай, если нечётное количество
        }

        int numTeams = teamList.size();
        int numRounds = numTeams - 1;

        List<List<Pair<TournamentTeam, TournamentTeam>>> rounds = new ArrayList<>();

        for (int round = 0; round < numRounds; round++) {
            List<Pair<TournamentTeam, TournamentTeam>> matches = new ArrayList<>();

            for (int i = 0; i < numTeams / 2; i++) {
                TournamentTeam teamA = teamList.get(i);
                TournamentTeam teamB = teamList.get(numTeams - 1 - i);

                if (teamA != null && teamB != null) {
                    matches.add(Pair.of(teamA, teamB));
                }
            }

            rounds.add(matches);

            // Поворот команд (фиксированная первая)
            teamList.add(1, teamList.remove(teamList.size() - 1));
        }

        return rounds;
    }


    @Override
    public List<List<List<Map<String, Object>>>> getBracket(TournamentStage stage) {
        List<TournamentMatch> matches = stage.getMatches();
        matches.sort(Comparator.comparingInt(TournamentMatch::getRound));
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
            matchData.add(processTeamData(match.getTeam1(), match.getTeam1Score(), match.getGroupLetter()));
            matchData.add(processTeamData(match.getTeam2(), match.getTeam2Score(), match.getGroupLetter()));

            currentRoundMatches.add(matchData);
        }

        return bracket;
    }

    private Map<String, Object> processTeamData(TournamentTeam team, int score, String group) {
        Map<String, Object> teamData = new HashMap<>();

        if (team != null) {
            teamData.put("seed", team.getSeed());
            teamData.put("name", team.getTeamName());
            teamData.put("score", score);
            teamData.put("group", group);
        } else {
            teamData.put("seed", 0);
            teamData.put("name", null);
            teamData.put("score", 0);
            teamData.put("group", null);
        }

        return teamData;
    }

    @Override
    public List<List<List<Map<String, Object>>>> updateBracket(List<List<List<Map<String, Object>>>> bracket, TournamentStage stage) {
        return List.of();
    }

    @Override
    public void advanceTeam(TournamentMatch match) {

    }

    @Override
    public List<TournamentMatch> findParentMatches(TournamentMatch match) {
        return Collections.emptyList();
    }

    @Override
    public List<TournamentMatch> findChildMatches(TournamentMatch match) {
        return Collections.emptyList();
    }

    @Override
    public List<TeamStandingsDto> getCurrentStandings(TournamentStage stage) {
        List<TournamentMatch> matches = stage.getMatches();
        matches.sort(Comparator.comparingInt(TournamentMatch::getRound));

        Map<String, Map<String, TeamStats>> groupStatsMap = new HashMap<>();
        Map<String, TournamentTeam> teamMap = new HashMap<>();
        Map<String, List<TournamentMatch>> teamMatchesMap = new HashMap<>();

        for (TournamentMatch match : matches) {
            String group = match.getGroupLetter();
            if (group == null) continue;

            TournamentTeam team1 = match.getTeam1();
            TournamentTeam team2 = match.getTeam2();

            if (team1 == null || team2 == null) continue;

            String team1Name = team1.getTeamName();
            String team2Name = team2.getTeamName();

            groupStatsMap.putIfAbsent(group, new HashMap<>());
            Map<String, TeamStats> statsMap = groupStatsMap.get(group);

            teamMap.putIfAbsent(team1Name, team1);
            teamMap.putIfAbsent(team2Name, team2);

            statsMap.putIfAbsent(team1Name, new TeamStats(group));
            statsMap.putIfAbsent(team2Name, new TeamStats(group));

            teamMatchesMap.computeIfAbsent(team1Name, k -> new ArrayList<>()).add(match);
            teamMatchesMap.computeIfAbsent(team2Name, k -> new ArrayList<>()).add(match);

            if (match.getWinnerTeamName() == null) continue;

            String winner = match.getWinnerTeamName();
            int team1Rounds = match.getWinRoundsTeam1();
            int team2Rounds = match.getWinRoundsTeam2();

            TeamStats stats1 = statsMap.get(team1Name);
            TeamStats stats2 = statsMap.get(team2Name);

            // Победы/поражения
            if (team1Name.equals(winner)) {
                stats1.incrementWins();
                stats2.incrementLosses();
            } else {
                stats2.incrementWins();
                stats1.incrementLosses();
            }

            // Разница раундов
            stats1.addRounds(team1Rounds, team2Rounds);
            stats2.addRounds(team2Rounds, team1Rounds);
        }

        List<TeamStandingsDto> result = new ArrayList<>();

        for (Map.Entry<String, Map<String, TeamStats>> groupEntry : groupStatsMap.entrySet()) {
            String group = groupEntry.getKey();
            Map<String, TeamStats> statsMap = groupEntry.getValue();

            List<Map.Entry<String, TeamStats>> sorted = statsMap.entrySet().stream()
                    .sorted((e1, e2) -> {
                        TeamStats s1 = e1.getValue();
                        TeamStats s2 = e2.getValue();
                        int cmp = Integer.compare(s2.getPoints(), s1.getPoints()); // по очкам
                        if (cmp != 0) return cmp;

                        // личка
                        TournamentMatch headToHead = findMatchBetween(e1.getKey(), e2.getKey(), teamMatchesMap);
                        if (headToHead != null && headToHead.getWinnerTeamName() != null) {
                            if (headToHead.getWinnerTeamName().equals(e1.getKey())) return -1;
                            if (headToHead.getWinnerTeamName().equals(e2.getKey())) return 1;
                        }

                        // разница раундов
                        return Integer.compare(s2.getRoundDifference(), s1.getRoundDifference());
                    })
                    .toList();

            int place = 1;
            for (Map.Entry<String, TeamStats> entry : sorted) {
                String teamName = entry.getKey();
                TeamStats stats = entry.getValue();
                TournamentTeam tournamentTeam = teamMap.get(teamName);
                TeamDto teamDto = teamMapper.toDto(tournamentTeam);

                result.add(TeamStandingsDto.builder()
                        .teamDto(teamDto)
                        .place(place++)
                        .wins(stats.getWins())
                        .losses(stats.getLosses())
                        .points(stats.getPoints())
                        .groupLetter(group)
                        .roundsWon(stats.getRoundsWon())
                        .roundsLost(stats.getRoundsLost())
                        .roundDifference(stats.getRoundDifference())
                        .build());
            }
        }

        return result;
    }

    private TournamentMatch findMatchBetween(String team1, String team2, Map<String, List<TournamentMatch>> matchMap) {
        List<TournamentMatch> matches = matchMap.getOrDefault(team1, List.of());
        for (TournamentMatch match : matches) {
            if (match.getTeam1() != null && match.getTeam2() != null) {
                String t1 = match.getTeam1().getTeamName();
                String t2 = match.getTeam2().getTeamName();
                if ((t1.equals(team1) && t2.equals(team2)) || (t1.equals(team2) && t2.equals(team1))) {
                    return match;
                }
            }
        }
        return null;
    }

}
