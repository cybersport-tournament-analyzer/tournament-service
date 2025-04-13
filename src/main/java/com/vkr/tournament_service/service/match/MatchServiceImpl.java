package com.vkr.tournament_service.service.match;

import com.vkr.tournament_service.dto.match.MatchDto;
import com.vkr.tournament_service.dto.team.TeamDto;
import com.vkr.tournament_service.entity.match.TournamentMatch;
import com.vkr.tournament_service.entity.schedule.ScheduleStatus;
import com.vkr.tournament_service.entity.schedule.TournamentSchedule;
import com.vkr.tournament_service.exception.EntityNotFoundException;
import com.vkr.tournament_service.exception.InvalidTournamentTimeException;
import com.vkr.tournament_service.exception.ValidationException;
import com.vkr.tournament_service.kafka.event.lobbyStart.LobbyStartEvent;
import com.vkr.tournament_service.kafka.event.matchEnd.MatchEndEvent;
import com.vkr.tournament_service.kafka.producer.lobbyStart.LobbyStartProducer;
import com.vkr.tournament_service.mapper.match.MatchMapper;
import com.vkr.tournament_service.mapper.team.TeamMapper;
import com.vkr.tournament_service.repository.match.MatchRepository;
import com.vkr.tournament_service.service.tournamentStage.TournamentStageManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchServiceImpl implements MatchService {

    private final MatchMapper matchMapper;
    private final LobbyStartProducer lobbyStartProducer;
    private final MatchRepository matchRepository;
    private final TeamMapper teamMapper;
    private final TournamentStageManager tournamentStageManager;

    @Override
    public void updateMatchResults(TournamentMatch match, MatchEndEvent event) {
        match.setTeam1Score(event.getTeam1Score());
        match.setTeam2Score(event.getTeam2Score());
        if (isSeriesFinished(match)) {
            match.getSchedule().setStatus(ScheduleStatus.COMPLETED);
            match.getSchedule().setActualEndTime(event.getEndTime());
            match.setWinnerTeamName(match.getTeam1Score() > match.getTeam2Score() ?
                    match.getTeam1().getTeamName() : match.getTeam2().getTeamName());
            tournamentStageManager.advanceTeam(match);
        }
        matchRepository.save(match);
    }

    @Override
    public List<TournamentMatch> getAllMatches() {
        return matchRepository.findAll();
    }

    @Override
    public MatchDto rescheduleMatch(UUID matchId, OffsetDateTime newStartTime, String userId) {
        TournamentMatch match = matchRepository.findById(matchId)
                .orElseThrow(() -> new EntityNotFoundException("Match with id: " + matchId + " not found!"));

        if (!userId.equals(match.getTournament().getCreatorId())) {
            throw new ValidationException("User with id=" + userId + " has no access to tournament with name=" + match.getTournament().getTournamentName());
        }

        TournamentSchedule schedule = match.getSchedule();

        if (schedule.getStatus() != ScheduleStatus.SCHEDULED) {
            throw new InvalidTournamentTimeException("You can change time only SCHEDULED matches");
        }

        List<TournamentMatch> parentMatches = tournamentStageManager.findParentMatches(match);
        List<TournamentMatch> childMatches = tournamentStageManager.findChildMatches(match);

        OffsetDateTime now = OffsetDateTime.now();
        if (newStartTime.isBefore(now)) {
            throw new InvalidTournamentTimeException("New start time must be in the future");
        }

        Duration buffer = getBufferForFormat(match.getMatchFormat());

        for (TournamentMatch parent : parentMatches) {
            TournamentSchedule parentSchedule = parent.getSchedule();
            // Используем фактическое время начала, если оно установлено, иначе запланированное
            OffsetDateTime parentStart = parentSchedule.getActualStartTime() != null
                    ? parentSchedule.getActualStartTime()
                    : parentSchedule.getScheduledStartTime();
            if (newStartTime.isBefore(parentStart.plus(buffer))) {
                throw new InvalidTournamentTimeException("New start time must be after parent's start time plus "
                        + buffer.toHours() + " hour(s)");
            }
        }

        for (TournamentMatch child : childMatches) {
            TournamentSchedule childSchedule = child.getSchedule();
            OffsetDateTime childStart = childSchedule.getScheduledStartTime();
            if (childStart != null && newStartTime.plus(buffer).isAfter(childStart)) {
                throw new InvalidTournamentTimeException("New start time plus " + buffer.toHours()
                        + " hour(s) must be before child's scheduled start time");
            }
        }

        schedule.setScheduledStartTime(newStartTime);
        return matchMapper.toDto(matchRepository.save(match));
    }

    @Override
    public MatchDto startTournamentMatch(UUID matchId, UUID tournamentId) {

        TournamentMatch tournamentMatch = matchRepository.findById(matchId).orElseThrow(() -> new EntityNotFoundException("Tournament match not found"));

        TeamDto team1 = teamMapper.toDto(tournamentMatch.getTeam1());
        TeamDto team2 = teamMapper.toDto(tournamentMatch.getTeam2());

        lobbyStartProducer.produce(new LobbyStartEvent(tournamentMatch.getId(),
                tournamentMatch.getTournament().getId(),
                tournamentMatch.getStage().getTournament().getTournamentMode(),
                tournamentMatch.getMatchFormat(),
                tournamentMatch.getSchedule().getScheduledStartTime().toLocalDateTime(), team1, team2, tournamentMatch.getTournament().getCreatorId())
        );

        log.info("Tournament match started");
        return matchMapper.toDto(tournamentMatch);

    }

    private boolean isSeriesFinished(TournamentMatch match) {
        String format = match.getMatchFormat().toLowerCase();
        int requiredWins = format.equals("bo3") ? 2 : format.equals("bo5") ? 3 : 1;
        return match.getTeam1Score() >= requiredWins || match.getTeam2Score() >= requiredWins;
    }

    private Duration getBufferForFormat(String matchFormat) {
        if (matchFormat == null) {
            return Duration.ofHours(1); // дефолтно 1 час
        }
        String format = matchFormat.toLowerCase();
        return switch (format) {
            case "bo3" -> Duration.ofHours(3);
            case "bo5" -> Duration.ofHours(5);
            default -> Duration.ofHours(1);
        };
    }

}
