package com.vkr.tournament_service.exception;

public class WrongTournamentStatusException extends RuntimeException{
    public WrongTournamentStatusException(String message) {
        super(message);
    }
}
