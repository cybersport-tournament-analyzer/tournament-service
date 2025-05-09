package com.vkr.tournament_service.service.player;

import com.vkr.tournament_service.dto.player.PlayerCreateDto;
import com.vkr.tournament_service.dto.player.PlayerRequestDto;
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

import java.util.UUID;

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
    public Player getPlayer(PlayerRequestDto playerRequestDto) {
        return playerRepository.findBySteamIdAndTeamId(playerRequestDto.getSteamId(), UUID.fromString(playerRequestDto.getTeamId())).orElseThrow(() ->
                new EntityNotFoundException("Player with id: " + playerRequestDto.getSteamId()
                        + " and teamId: " + playerRequestDto.getTeamId() + " not found."));
    }

    @Override
    @Transactional
    public Player updatePlayerRole(String userId, PlayerUpdateDto playerUpdateDto) {
        playerValidator.validateAccess(playerUpdateDto.getSteamId(), userId, playerUpdateDto.getTeamId());
        Player player = playerRepository.findBySteamIdAndTeamId(playerUpdateDto.getSteamId()
                , UUID.fromString(playerUpdateDto.getTeamId())).orElseThrow(() ->
                new EntityNotFoundException("Player with id: " + playerUpdateDto.getSteamId()
                        + " and teamId: " + playerUpdateDto.getTeamId() + " not found."));

        player.setInGameRole(InGameRole.fromString(playerUpdateDto.getInGameRole()));
        return playerRepository.save(player);
    }

}
