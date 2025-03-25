package com.vkr.tournament_service.exception;

public class InvalidTournamentTimeException extends RuntimeException {
    public InvalidTournamentTimeException(String message) {
        super(message);
    }
}
