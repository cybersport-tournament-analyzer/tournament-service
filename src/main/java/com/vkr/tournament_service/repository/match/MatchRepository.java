package com.vkr.tournament_service.repository.match;

import com.vkr.tournament_service.entity.match.TournamentMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MatchRepository extends JpaRepository<TournamentMatch, UUID> {
    List<TournamentMatch> findAllByTournamentIdOrderByRoundAsc(UUID tournamentId);
}
