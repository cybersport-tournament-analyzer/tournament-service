package com.vkr.tournament_service.service.player;

import com.vkr.tournament_service.dto.player.PlayerCreateDto;
import com.vkr.tournament_service.dto.player.PlayerUpdateDto;
import com.vkr.tournament_service.entity.player.InGameRole;
import com.vkr.tournament_service.entity.player.Player;
import com.vkr.tournament_service.exception.EntityNotFoundException;
import com.vkr.tournament_service.mapper.player.PlayerMapper;
import com.vkr.tournament_service.repository.player.PlayerRepository;
import com.vkr.tournament_service.validator.player.PlayerValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;
    private final PlayerValidator playerValidator;

    @Override
    public Player createPlayer(PlayerCreateDto playerCreateDto) {
        log.info("Creating player: {}", playerCreateDto);
        return playerRepository.save(playerMapper.toEntity(playerCreateDto));
    }

    @Override
    public Player getPlayer(String playerSteamId) {
        return playerRepository.findByPlayerSteamId(playerSteamId);
    }

    @Override
    @Transactional
    public Player updatePlayerRole(String userId, PlayerUpdateDto playerUpdateDto) {
        playerValidator.validateAccess(playerUpdateDto.getSteamId(), userId);
        Player player = playerRepository.findByPlayerSteamId(playerUpdateDto.getSteamId());
        if (player == null) {
            throw new EntityNotFoundException("Player with Steam ID " + playerUpdateDto.getSteamId() + " not found");
        }

        player.setInGameRole(InGameRole.fromString(playerUpdateDto.getInGameRole()));
        return playerRepository.save(player);
    }

}
