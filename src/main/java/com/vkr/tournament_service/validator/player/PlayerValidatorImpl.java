package com.vkr.tournament_service.validator.player;

import com.vkr.tournament_service.entity.player.Player;
import com.vkr.tournament_service.exception.ValidationException;
import com.vkr.tournament_service.repository.player.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlayerValidatorImpl implements PlayerValidator {

    private final PlayerRepository playerRepository;

    @Override
    public void validateAccess(String playerSteamId, String userId) {
        Player player = playerRepository.findByPlayerSteamId(playerSteamId);
        if (!player.getPlayerSteamId().equals(userId)) {
            throw new ValidationException("User with id=" + userId + " can't change roles of other users.");
        }
    }
}
