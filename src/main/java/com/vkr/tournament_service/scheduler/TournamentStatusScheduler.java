package com.vkr.tournament_service.scheduler;


import com.vkr.tournament_service.entity.tournament.Tournament;
import com.vkr.tournament_service.entity.tournament.TournamentStatus;
import com.vkr.tournament_service.repository.tournament.TournamentRepository;
import com.vkr.tournament_service.service.tournament.TournamentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.Mapping;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TournamentStatusScheduler {

    private final TournamentRepository tournamentRepository;

    private final TournamentService tournamentService;

    @Scheduled(fixedRate = 10000) // Запуск каждые 10 секунд
    public void updateTournamentStatuses() {
        OffsetDateTime now = OffsetDateTime.now();
        List<Tournament> tournaments = tournamentRepository.findAll();

        for (Tournament tournament : tournaments) {
            if (tournament.getTournamentStatus() == TournamentStatus.NOT_STARTED) {
                if (now.isAfter(tournament.getRegistrationStartTime())) {
                    tournament.setTournamentStatus(TournamentStatus.REGISTRATION);
                    log.info("Tournament {} status updated to REGISTRATION", tournament.getTournamentName());
                }
            }

            if (tournament.getTournamentStatus() == TournamentStatus.REGISTRATION) {
                if (now.isAfter(tournament.getRegistrationEndTime())) {
                    if(tournament.getTeams().isEmpty() || tournament.getTeams().size() != tournament.getTeamsCount()) {tournamentService.deleteTournament(
                            String.valueOf(tournament.getId()), tournament.getCreatorId() //защита от долбаебов
                    );
                    } else {
                        tournament.setTournamentStatus(TournamentStatus.REGISTRATION_ENDED);
                        log.info("Tournament {} status updated to REGISTRATION_ENDED", tournament.getTournamentName());
                        tournamentService.setTeamsSeeds(tournament);
                        tournamentService.startFirstStage(tournament);
                    }
                }
            }
            if (tournament.getTournamentStatus() == TournamentStatus.REGISTRATION_ENDED) {
                if (now.isAfter(tournament.getTournamentStartTime())) {
                    tournament.setTournamentStatus(TournamentStatus.ACTIVE);
                    log.info("Tournament {} status updated to ACTIVE", tournament.getTournamentName());
                }
            }
        }

        tournamentRepository.saveAll(tournaments);
    }
}

