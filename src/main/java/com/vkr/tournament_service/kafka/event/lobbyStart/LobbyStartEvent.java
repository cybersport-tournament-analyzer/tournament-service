package com.vkr.tournament_service.kafka.event.lobbyStart;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vkr.tournament_service.dto.team.TeamDto;
import com.vkr.tournament_service.kafka.event.KafkaEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Jacksonized
@AllArgsConstructor
public class LobbyStartEvent implements KafkaEvent {
    private UUID tournamentMatchId;
    private UUID tournamentId;
    private String mode;
    private String format;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;
    private TeamDto team1;
    private TeamDto team2;
    private String adminId;
}