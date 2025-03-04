package com.vkr.tournament_service.dto.player;

import com.vkr.tournament_service.entity.player.InGameRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayerUpdateDto {
    private List<InGameRole> inGameRoles;
}
