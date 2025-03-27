package com.vkr.tournament_service.exception;

public class TeamNameAlreadyInUseException extends RuntimeException {
    public TeamNameAlreadyInUseException(String message) {
        super(message);
    }
}
