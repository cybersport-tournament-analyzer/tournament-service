package com.vkr.tournament_service.dto.player;

import com.vkr.tournament_service.entity.player.InGameRole;
import com.vkr.tournament_service.entity.player.PlayerStats;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Jacksonized
public class PlayerDto {
    private UUID id;
    private String playerSteamId;
    private List<InGameRole> inGameRoles;
}
