package com.vkr.tournament_service.repository.team;

import com.vkr.tournament_service.entity.team.TournamentTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeamRepository extends JpaRepository<TournamentTeam, UUID> {
    TournamentTeam findByTeamName(String teamName);

    List<TournamentTeam> findAllByTournamentId(UUID tournamentId);

    TournamentTeam findByTeamNameAndTournamentId(String teamName, UUID tournamentId);
}
