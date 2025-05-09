package com.vkr.tournament_service.validator.player;

public interface PlayerValidator {
    void validateAccess(String playerSteamId, String userId, String teamId);
}
