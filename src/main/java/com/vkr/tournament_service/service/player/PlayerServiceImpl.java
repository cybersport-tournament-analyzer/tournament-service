package com.vkr.tournament_service.service.player;

import com.vkr.tournament_service.dto.player.PlayerCreateDto;
import com.vkr.tournament_service.entity.player.Player;
import com.vkr.tournament_service.mapper.player.PlayerMapper;
import com.vkr.tournament_service.repository.player.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;

    @Override
    public Player createPlayer(PlayerCreateDto playerCreateDto) {
        log.info("Creating player: {}", playerCreateDto);
        return playerRepository.save(playerMapper.toEntity(playerCreateDto));
    }

    @Override
    public Player getPlayer(String playerSteamId) {
        return playerRepository.findByPlayerSteamId(playerSteamId);
    }
}
