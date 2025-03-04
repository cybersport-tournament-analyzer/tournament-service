package com.vkr.tournament_service.repository.team;

import com.vkr.tournament_service.entity.match.Match;
import com.vkr.tournament_service.entity.team.TournamentTeam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TeamRepository extends JpaRepository<TournamentTeam, UUID> {
    TournamentTeam findByTeamName(String teamName);

    Page<TournamentTeam> findAllByTournamentId(UUID tournamentId, Pageable pageable);
}
