package com.vkr.tournament_service.kafka.consumer.match;

import com.vkr.tournament_service.entity.match.TournamentMatch;
import com.vkr.tournament_service.entity.schedule.ScheduleStatus;
import com.vkr.tournament_service.exception.KafkaConsumerException;
import com.vkr.tournament_service.kafka.consumer.KafkaConsumer;
import com.vkr.tournament_service.kafka.event.matchEnd.MatchEndEvent;
import com.vkr.tournament_service.kafka.event.matchStart.MatchStartEvent;
import com.vkr.tournament_service.repository.match.MatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchStartConsumer implements KafkaConsumer<MatchStartEvent> {

    private final MatchRepository matchRepository;

    @Override
    @Transactional
    @KafkaListener(topics = "${spring.data.kafka.topics.topic-settings.match-start.name}", groupId = "${spring.data.kafka.group-id}")
    public void consume(MatchStartEvent event, Acknowledgment ack) {
        log.info("Consuming match start event: {}", event);

        TournamentMatch currMatch = matchRepository.findById(event.getTournamentMatchId()).orElse(null);

        try {
            assert currMatch != null;
            if(currMatch.getSchedule().getActualStartTime() == null){
                currMatch.getSchedule().setStatus(ScheduleStatus.IN_PROGRESS);
                currMatch.getSchedule().setActualStartTime(event.getStartTime());
                matchRepository.save(currMatch);
            }
            ack.acknowledge();
        } catch (Exception e) {
            throw new KafkaConsumerException(e);

        }
    }

}
