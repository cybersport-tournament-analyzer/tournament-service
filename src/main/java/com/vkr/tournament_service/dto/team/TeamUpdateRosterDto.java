package com.vkr.tournament_service.dto.team;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamUpdateRosterDto {
    private String userId;
    private List<String> playersIds;
}
