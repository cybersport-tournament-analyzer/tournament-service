package com.vkr.tournament_service.controller.player;

import com.vkr.tournament_service.dto.player.PlayerCreateDto;
import com.vkr.tournament_service.dto.player.PlayerDto;
import com.vkr.tournament_service.service.player.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @GetMapping("/{playerUsername}")
    public PlayerDto getPlayer(@PathVariable String playerUsername) {
        return playerService.getPlayer(playerUsername);
    }

    @PostMapping
    public PlayerDto createPlayer(@RequestBody PlayerCreateDto playerDto) {
        return playerService.createPlayer(playerDto);
    }
}
