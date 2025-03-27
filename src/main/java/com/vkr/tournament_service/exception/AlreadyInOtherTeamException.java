package com.vkr.tournament_service.exception;

public class AlreadyInOtherTeamException extends RuntimeException {
    public AlreadyInOtherTeamException(String message) {
        super(message);
    }
}
