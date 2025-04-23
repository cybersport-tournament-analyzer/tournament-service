package com.vkr.tournament_service.kafka.event.matchEnd;


import com.vkr.tournament_service.dto.matchmaking.Match;
import com.vkr.tournament_service.kafka.event.KafkaEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@Jacksonized
@AllArgsConstructor
public class MatchEndEvent implements KafkaEvent {
    private UUID tournamentMatchId;
    private UUID tournamentId;
    private int team1Score;
    private int team2Score;
    private OffsetDateTime endTime;
    private Match match;
}