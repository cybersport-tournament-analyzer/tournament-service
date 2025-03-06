package com.vkr.tournament_service.service.match;

import com.vkr.tournament_service.dto.match.MatchCreateDto;
import com.vkr.tournament_service.dto.match.MatchDto;

import java.util.UUID;

public interface MatchService {

    MatchDto startTournamentMatch(MatchCreateDto matchCreateDto, UUID tournamentId);
}
