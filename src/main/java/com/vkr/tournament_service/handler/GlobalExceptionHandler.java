package com.vkr.tournament_service.handler;

import com.vkr.tournament_service.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, List<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return e.getBindingResult().getAllErrors().stream()
                .collect(Collectors.groupingBy(error -> ((FieldError) error).getField(),
                        Collectors.mapping(error -> Objects.requireNonNullElse(error.getDefaultMessage(), ""),
                                Collectors.toList()))
                );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(EntityNotFoundException e, HttpServletRequest request) {
        log.error("Not found: {}", e.getMessage());
        return buildErrorResponse(e, request);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.error("Runtime exception: {}", e.getMessage(), e);
        return buildErrorResponse(e, request);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataValidationException(ValidationException e, HttpServletRequest request) {
        log.error("Data validation error: {}", e.getMessage());
        return buildErrorResponse(e, request);
    }

    @ExceptionHandler(InvalidTournamentTimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidTimesException(InvalidTournamentTimeException e, HttpServletRequest request) {
        log.error("Invalid times error: {}", e.getMessage());
        return buildErrorResponse(e, request);
    }

    @ExceptionHandler(WrongTournamentStatusException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleWrongTournamentStatusException(WrongTournamentStatusException e, HttpServletRequest request) {
        log.error("Wrong tournament status error: {}", e.getMessage());
        return buildErrorResponse(e, request);
    }

    @ExceptionHandler(TeamListIsFullException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleTeamListIsFullException(TeamListIsFullException e, HttpServletRequest request) {
        log.error("Tournament teams list error: {}", e.getMessage());
        return buildErrorResponse(e, request);
    }

    @ExceptionHandler(AlreadyInOtherTeamException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleAlreadyInOtherTeamException(AlreadyInOtherTeamException e, HttpServletRequest request) {
        log.error("Tournament team error: {}", e.getMessage());
        return buildErrorResponse(e, request);
    }

    @ExceptionHandler(TeamNameAlreadyInUseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleTeamNameAlreadyInUseException(TeamNameAlreadyInUseException e, HttpServletRequest request) {
        log.error("Team name error: {}", e.getMessage());
        return buildErrorResponse(e, request);
    }

    @ExceptionHandler(TeamIsFullException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleTeamIsFullException(TeamIsFullException e, HttpServletRequest request) {
        log.error("Team size error: {}", e.getMessage());
        return buildErrorResponse(e, request);
    }

    private ErrorResponse buildErrorResponse(Exception e, HttpServletRequest request) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .url(request.getRequestURI())
                .message(e.getMessage())
                .build();
    }
}