package com.vkr.tournament_service.dto.match;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Jacksonized
public class MatchCreateDto {
    @NotNull
    private UUID tournamentId;
    @NotBlank
    private String matchFormat;
    @NotBlank
    private String team1Name;
    @NotBlank
    private String team2Name;
}
