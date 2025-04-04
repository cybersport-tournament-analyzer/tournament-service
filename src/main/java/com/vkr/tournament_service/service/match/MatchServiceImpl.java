package com.vkr.tournament_service.service.match;

import com.vkr.tournament_service.dto.match.MatchDto;
import com.vkr.tournament_service.entity.match.TournamentMatch;
import com.vkr.tournament_service.entity.tournament.Tournament;
import com.vkr.tournament_service.exception.EntityNotFoundException;
import com.vkr.tournament_service.kafka.event.lobbyStart.LobbyStartEvent;
import com.vkr.tournament_service.kafka.producer.lobbyStart.LobbyStartProducer;
import com.vkr.tournament_service.mapper.match.MatchMapper;
import com.vkr.tournament_service.repository.match.MatchRepository;
import com.vkr.tournament_service.service.team.TeamService;
import com.vkr.tournament_service.service.tournament.TournamentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchServiceImpl implements MatchService {

    private final MatchMapper matchMapper;
    private final LobbyStartProducer lobbyStartProducer;
    private final MatchRepository matchRepository;
    private final TournamentService tournamentService;

    @Override
    public MatchDto startTournamentMatch(UUID matchId, UUID tournamentId) {

        TournamentMatch tournamentMatch = matchRepository.findById(matchId).orElseThrow(() -> new EntityNotFoundException("Tournament match not found"));

        System.out.println(tournamentMatch.getTournament().getTournamentMode());
        System.out.println(tournamentMatch.getTournament());

        lobbyStartProducer.produce(new LobbyStartEvent(tournamentMatch.getId(),
                tournamentService.getTournamentById(tournamentId).getTournamentMode(),
                tournamentMatch.getMatchFormat(),
                tournamentMatch.getSchedule().getScheduledStartTime().toLocalDateTime())
        );

        log.info("Tournament match started");
        return matchMapper.toDto(tournamentMatch);

    }
}
