package com.vkr.tournament_service.service.tournament;

import com.vkr.tournament_service.dto.tournament.TournamentCreateDto;
import com.vkr.tournament_service.dto.tournament.TournamentDto;
import com.vkr.tournament_service.dto.tournament.TournamentUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TournamentService {

    TournamentDto getTournamentByName(String tournamentName);

    Page<TournamentDto> getAllTournaments(Pageable pageable);

    TournamentDto createTournament(TournamentCreateDto tournamentCreateDto);

    TournamentDto updateTournament(TournamentUpdateDto tournamentUpdateDto, String tournamentName);

    void deleteTournament(String tournamentName);
}
