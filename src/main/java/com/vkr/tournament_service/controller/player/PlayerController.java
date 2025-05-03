package com.vkr.tournament_service.controller.player;

import com.vkr.tournament_service.dto.player.PlayerCreateDto;
import com.vkr.tournament_service.dto.player.PlayerDto;
import com.vkr.tournament_service.dto.player.PlayerUpdateDto;
import com.vkr.tournament_service.mapper.player.PlayerMapper;
import com.vkr.tournament_service.service.player.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;
    private final PlayerMapper playerMapper;

    @GetMapping("/{playerUsername}")
    public PlayerDto getPlayer(@PathVariable String playerUsername) {
        return playerMapper.toDto(playerService.getPlayer(playerUsername));
    }

    @PostMapping
    public PlayerDto createPlayer(@RequestBody PlayerCreateDto playerDto) {
        return playerMapper.toDto(playerService.createPlayer(playerDto));
    }

    @PutMapping
    public PlayerDto updatePlayer(@RequestBody PlayerUpdateDto playerUpdateDto, @RequestParam String userId) {
        return playerMapper.toDto(playerService.updatePlayerRole(userId, playerUpdateDto));
    }
}
