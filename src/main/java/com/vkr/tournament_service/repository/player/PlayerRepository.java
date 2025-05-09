package com.vkr.tournament_service.repository.player;

import com.vkr.tournament_service.entity.player.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlayerRepository extends JpaRepository<Player, UUID> {

    @Query("""
                SELECT p FROM Player p 
                JOIN p.teams t 
                WHERE p.playerSteamId = :steamId 
                  AND t.id = :teamId
            """)
    Optional<Player> findBySteamIdAndTeamId(@Param("steamId") String steamId, @Param("teamId") UUID teamId);
}
