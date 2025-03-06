package com.vkr.tournament_service.kafka.producer;

import com.vkr.tournament_service.kafka.event.KafkaEvent;

public interface KafkaProducer<T extends KafkaEvent> {
    void produce(T event);

    void produce(T event, Runnable runnable);
}
