package com.vkr.tournament_service.kafka.producer.tournamentStart;

import com.vkr.tournament_service.kafka.event.tournamentStart.TournamentStartEvent;
import com.vkr.tournament_service.kafka.producer.AbstractKafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class TournamentStartProducer extends AbstractKafkaProducer<TournamentStartEvent> {

    @Value("${spring.data.kafka.topics.topic-settings.tournament-start.name}")
    private String channelTopic;

    public TournamentStartProducer(KafkaTemplate<String, Object> kafkaTemplate,
                              Map<String, NewTopic> topicMap) {
        super(kafkaTemplate, topicMap);
    }

    @Override
    public String getTopic() {
        return channelTopic;
    }
}
