package com.vkr.tournament_service.repository.tournament;

import com.vkr.tournament_service.entity.tournament.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, UUID> {
    Optional<Tournament> findByTournamentName(String tournamentName);

}
