package com.vkr.tournament_service.service.match;

import com.vkr.tournament_service.dto.match.MatchDto;
import com.vkr.tournament_service.entity.match.TournamentMatch;
import com.vkr.tournament_service.kafka.event.matchEnd.MatchEndEvent;

import java.util.UUID;

public interface MatchService {

    void updateMatchResults(TournamentMatch match, MatchEndEvent event);

    MatchDto startTournamentMatch(UUID matchId, UUID tournamentId);
}
