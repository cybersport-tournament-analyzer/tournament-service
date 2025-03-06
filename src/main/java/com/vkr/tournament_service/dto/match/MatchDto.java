package com.vkr.tournament_service.dto.match;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vkr.tournament_service.entity.match.Match;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Jacksonized
public class MatchDto {
    private UUID id;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;
    private UUID tournamentId;
    private String matchFormat;
    private String team1Name;
    private String team2Name;
    private int team1Score;
    private int team2Score;
    private String matchStatus;
    private List<Match> matches;
}
