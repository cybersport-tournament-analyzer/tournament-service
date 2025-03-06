package com.vkr.tournament_service.kafka.producer.lobbyStart;

import com.vkr.tournament_service.kafka.event.lobbyStart.LobbyStartEvent;
import com.vkr.tournament_service.kafka.producer.AbstractKafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class LobbyStartProducer extends AbstractKafkaProducer<LobbyStartEvent> {

    @Value("${spring.data.kafka.topics.topic-settings.lobby-start.name}")
    private String channelTopic;

    public LobbyStartProducer(KafkaTemplate<String, Object> kafkaTemplate,
                         Map<String, NewTopic> topicMap) {
        super(kafkaTemplate, topicMap);
    }

    @Override
    public String getTopic() {
        return channelTopic;
    }
}
