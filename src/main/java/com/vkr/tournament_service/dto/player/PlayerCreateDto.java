package com.vkr.tournament_service.dto.player;

import com.vkr.tournament_service.entity.player.InGameRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Jacksonized
public class PlayerCreateDto {
    private String playerUsername;
    private String playerSteamId;
    private int rating;
    private List<InGameRole> inGameRoles;
}
