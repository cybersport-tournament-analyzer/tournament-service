package com.vkr.tournament_service.kafka.event.tournamentStart;

import com.vkr.tournament_service.kafka.event.KafkaEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@Jacksonized
@AllArgsConstructor
public class TournamentStartEvent implements KafkaEvent {
    private UUID tournamentId;
    List<String> playerIds;
}
