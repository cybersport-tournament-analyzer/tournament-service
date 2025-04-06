package com.vkr.tournament_service.scheduler;

import com.vkr.tournament_service.entity.match.TournamentMatch;
import com.vkr.tournament_service.entity.schedule.ScheduleStatus;
import com.vkr.tournament_service.entity.schedule.TournamentSchedule;
import com.vkr.tournament_service.service.match.MatchService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchStartScheduler {
    private final MatchService matchService;

    @Scheduled(fixedRate = 60000) // каждые 60 секунд
    @Transactional
    public void startScheduledMatches() {
        OffsetDateTime now = OffsetDateTime.now();

        List<TournamentMatch> matchesToStart = matchService.getAllMatches();

        for (TournamentMatch match : matchesToStart) {
            TournamentSchedule schedule = match.getSchedule();

            if (schedule.getStatus() == ScheduleStatus.SCHEDULED &&
                    schedule.getScheduledStartTime().isBefore(now)) {
                matchService.startTournamentMatch(match.getId(), match.getTournament().getId());
            }
        }
    }
}
