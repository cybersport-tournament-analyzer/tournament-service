package com.vkr.tournament_service.repository.player;

import com.vkr.tournament_service.entity.player.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlayerRepository extends JpaRepository<Player, UUID> {
    Player findByPlayerSteamId(String playerSteamId);

}
