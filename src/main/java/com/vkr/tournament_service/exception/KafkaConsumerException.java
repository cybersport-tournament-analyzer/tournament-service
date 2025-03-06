package com.vkr.tournament_service.exception;

public class KafkaConsumerException extends RuntimeException {
    public KafkaConsumerException(Throwable cause) {
        super(cause);
    }
}

