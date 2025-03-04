package com.vkr.tournament_service.service.tournament;

import com.vkr.tournament_service.dto.tournament.TournamentCreateDto;
import com.vkr.tournament_service.dto.tournament.TournamentDto;
import com.vkr.tournament_service.dto.tournament.TournamentUpdateDto;
import com.vkr.tournament_service.entity.tournament.Stage;
import com.vkr.tournament_service.entity.tournament.Tournament;
import com.vkr.tournament_service.entity.tournament.TournamentStatus;
import com.vkr.tournament_service.exception.EntityNotFoundException;
import com.vkr.tournament_service.mapper.tournament.TournamentMapper;
import com.vkr.tournament_service.repository.tournament.TournamentRepository;
import com.vkr.tournament_service.validator.tournament.TournamentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentServiceImpl implements TournamentService {

    private final TournamentRepository tournamentRepository;
    private final TournamentMapper tournamentMapper;
    private final TournamentValidator tournamentValidator;

    @Override
    public TournamentDto getTournamentByName(String tournamentName) {
        Tournament tournament = getTournament(tournamentName);
        return tournamentMapper.toDto(tournament);
    }

    @Override
    public Page<TournamentDto> getAllTournaments(Pageable pageable) {
        return tournamentRepository.findAll(pageable).map(tournamentMapper::toDto);
    }

    @Override
    public TournamentDto createBaseTournament(TournamentCreateDto tournamentCreateDto) {
        Tournament tournament = tournamentMapper.toEntity(tournamentCreateDto);

        tournament.setTournamentStatus(TournamentStatus.ACTIVE);
        tournament.setCurrentStageName("not started");

        List<String> stageNames = tournamentCreateDto.getStages();
        List<Stage> stages = stageNames.stream()
                .map(stage -> Stage.fromName(stage))
                .collect(Collectors.toList());
        tournament.setStages(stages);

        tournamentRepository.save(tournament);

        log.info("Tournament created: {}", tournament);

        return tournamentMapper.toDto(tournament);
    }

    @Override
    public TournamentDto updateTournament(TournamentUpdateDto tournamentUpdateDto, String tournamentName) {
        Tournament tournament = getTournament(tournamentName);

        tournamentValidator.validateAccess(tournament.getId(), tournament.getCreatorUsername());

        tournament = tournamentMapper.updateEntity(tournamentUpdateDto, tournament);

        tournamentRepository.save(tournament);

        log.info("Tournament updated: {}", tournament);

        return tournamentMapper.toDto(tournament);
    }

    @Override
    public void deleteTournament(String tournamentName) {
        Tournament tournament = getTournament(tournamentName);

        tournamentValidator.validateAccess(tournament.getId(), tournament.getCreatorUsername());

        tournamentRepository.delete(tournament);

        log.info("Tournament deleted: {}", tournament);
    }


    private Tournament getTournament(String tournamentName) {
        return tournamentRepository.findByTournamentName(tournamentName).orElseThrow(
                () -> new EntityNotFoundException("Tournament with name=" + tournamentName + " not found")
        );
    }
}
