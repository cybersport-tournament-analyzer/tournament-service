package com.vkr.tournament_service.service.player;

import com.vkr.tournament_service.dto.player.PlayerCreateDto;
import com.vkr.tournament_service.dto.player.PlayerUpdateDto;
import com.vkr.tournament_service.entity.player.InGameRole;
import com.vkr.tournament_service.entity.player.Player;

public interface PlayerService {

    Player createPlayer(PlayerCreateDto playerCreateDto);

    Player getPlayer(String playerSteamId);

    Player updatePlayerRole(String userId, PlayerUpdateDto playerUpdateDto);
}
