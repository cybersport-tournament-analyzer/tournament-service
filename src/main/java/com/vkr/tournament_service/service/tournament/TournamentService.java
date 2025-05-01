package com.vkr.tournament_service.service.tournament;

import com.vkr.tournament_service.dto.tournament.TournamentCreateDto;
import com.vkr.tournament_service.dto.tournament.TournamentDto;
import com.vkr.tournament_service.dto.tournament.TournamentUpdateDto;
import com.vkr.tournament_service.entity.team.TournamentTeam;
import com.vkr.tournament_service.entity.tournament.Tournament;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface TournamentService {

    TournamentDto getTournamentByName(String tournamentName);

    Page<TournamentDto> getAllTournaments(Pageable pageable);

    TournamentDto createBaseTournament(TournamentCreateDto tournamentCreateDto);

    TournamentDto updateTournament(TournamentUpdateDto tournamentUpdateDto, String tournamentId);

    void deleteTournament(String tournamentId, String userId);

    Tournament getTournamentById(UUID tournamentId);

    TournamentDto startTournamentRegistration(String tournamentId, String userId);

    TournamentDto stopTournamentRegistration(String tournamentId, String userId);

    void setTeamsSeeds(Tournament tournament);

    Tournament getTournament(String tournamentName);

    void startFirstStage(Tournament tournament);

    void startNextStage(Tournament tournament, List<TournamentTeam> teams);
}
