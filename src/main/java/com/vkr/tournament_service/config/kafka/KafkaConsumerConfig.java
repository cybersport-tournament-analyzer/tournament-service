package com.vkr.tournament_service.config.kafka;

import com.vkr.tournament_service.entity.match.Match;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

@Configuration
public class KafkaConsumerConfig {
    @KafkaListener(topics = "match-finished-topic", groupId = "tournament-group")
    public void consume(Match event) {
        System.out.println("Получено событие о матче: " + event);
    }
}
