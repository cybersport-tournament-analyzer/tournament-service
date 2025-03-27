package com.vkr.tournament_service.exception;

public class TeamListIsFullException extends RuntimeException{
    public TeamListIsFullException(String message) {
        super(message);
    }
}
