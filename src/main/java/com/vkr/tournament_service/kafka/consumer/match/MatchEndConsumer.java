package com.vkr.tournament_service.kafka.consumer.match;

import com.vkr.tournament_service.entity.match.TournamentMatch;
import com.vkr.tournament_service.exception.EntityNotFoundException;
import com.vkr.tournament_service.exception.KafkaConsumerException;
import com.vkr.tournament_service.kafka.consumer.KafkaConsumer;
import com.vkr.tournament_service.kafka.event.matchEnd.MatchEndEvent;
import com.vkr.tournament_service.repository.match.MatchRepository;
import com.vkr.tournament_service.service.match.MatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchEndConsumer implements KafkaConsumer<MatchEndEvent> {

    private final MatchRepository matchRepository;
    private final MatchService matchService;

    @Override
    @Transactional
    @KafkaListener(topics = "${spring.data.kafka.topics.topic-settings.match-end.name}", groupId = "${spring.data.kafka.group-id}")
    public void consume(MatchEndEvent event, Acknowledgment ack) {
        log.info("Consuming match end event: {}", event);

        TournamentMatch currMatch = matchRepository.findById(event.getTournamentMatchId()).orElseThrow(
                () -> new EntityNotFoundException("Match not found: " + event.getTournamentMatchId()));

        try {
            matchService.updateMatchResults(currMatch, event);
            ack.acknowledge();
        } catch (Exception e) {
            throw new KafkaConsumerException(e);

        }
    }

}