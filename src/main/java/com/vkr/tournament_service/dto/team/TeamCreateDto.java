package com.vkr.tournament_service.dto.team;

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
public class TeamCreateDto {
    private String teamName;
    private String flag;
    private String creatorSteamId;
    private List<String> steamIds;
}
