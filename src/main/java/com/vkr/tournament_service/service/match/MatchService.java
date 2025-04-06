package com.vkr.tournament_service.service.match;

import com.vkr.tournament_service.dto.match.MatchDto;
import com.vkr.tournament_service.entity.match.TournamentMatch;
import com.vkr.tournament_service.kafka.event.matchEnd.MatchEndEvent;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface MatchService {

    void updateMatchResults(TournamentMatch match, MatchEndEvent event);
    List<TournamentMatch> getAllMatches();
    void rescheduleMatch(UUID matchId, OffsetDateTime newStartTime);

    MatchDto startTournamentMatch(UUID matchId, UUID tournamentId);
}
