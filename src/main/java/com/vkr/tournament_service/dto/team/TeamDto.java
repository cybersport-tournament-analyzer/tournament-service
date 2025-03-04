package com.vkr.tournament_service.dto.team;

import com.vkr.tournament_service.dto.player.PlayerDto;
import com.vkr.tournament_service.entity.player.Player;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Jacksonized
public class TeamDto {
    private UUID id;
    private UUID tournamentId;
    private String teamName;
    private String flag;
    private String creatorUsername;
    private List<PlayerDto> players = new ArrayList<>();
}
