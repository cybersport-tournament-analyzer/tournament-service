package com.vkr.tournament_service.kafka.event.matchStart;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class MatchStartEvent implements KafkaEvent {
    private UUID tournamentMatchId;
    private OffsetDateTime startTime;
}
