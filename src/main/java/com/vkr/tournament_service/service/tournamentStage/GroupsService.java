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

        for (TournamentMatch match : matches) {
            String group = match.getGroupLetter();
            if (group == null) continue;

            TournamentTeam team1 = match.getTeam1();
            TournamentTeam team2 = match.getTeam2();

            groupStatsMap.putIfAbsent(group, new HashMap<>());
            Map<String, TeamStats> statsMap = groupStatsMap.get(group);

            if (team1 != null) {
                statsMap.putIfAbsent(team1.getTeamName(), new TeamStats(group));
                teamMap.putIfAbsent(team1.getTeamName(), team1);
            }

            if (team2 != null) {
                statsMap.putIfAbsent(team2.getTeamName(), new TeamStats(group));
                teamMap.putIfAbsent(team2.getTeamName(), team2);
            }

            // Пропускаем если матч не завершён
            if (match.getWinnerTeamName() == null) continue;

            String winner = match.getWinnerTeamName();

            TeamStats team1Stats = statsMap.get(team1.getTeamName());
            TeamStats team2Stats = statsMap.get(team2.getTeamName());

            if (team1.getTeamName().equals(winner)) {
                team1Stats.incrementWins();
                team2Stats.incrementLosses();
            } else if (team2.getTeamName().equals(winner)) {
                team2Stats.incrementWins();
                team1Stats.incrementLosses();
            }
        }

        List<TeamStandingsDto> result = new ArrayList<>();

        for (Map.Entry<String, Map<String, TeamStats>> groupEntry : groupStatsMap.entrySet()) {
            String group = groupEntry.getKey();
            Map<String, TeamStats> statsMap = groupEntry.getValue();

            List<Map.Entry<String, TeamStats>> sorted = statsMap.entrySet().stream()
                    .sorted(Comparator.comparingInt((Map.Entry<String, TeamStats> e) -> e.getValue().getWins()).reversed())
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
                        .points(stats.getWins() * 2)
                        .groupLetter(group)
                        .build());
            }
        }

        return result;
    }


}
