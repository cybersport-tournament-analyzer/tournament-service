package com.vkr.tournament_service.service.player;

import com.vkr.tournament_service.dto.player.PlayerCreateDto;
import com.vkr.tournament_service.dto.player.PlayerDto;

public interface PlayerService {

    PlayerDto createPlayer(PlayerCreateDto playerCreateDto);

    PlayerDto getPlayer(String playerUsername);
}
