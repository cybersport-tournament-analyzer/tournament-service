package com.vkr.tournament_service.mapper.team;

import com.vkr.tournament_service.dto.team.TeamCreateDto;
import com.vkr.tournament_service.dto.team.TeamDto;
import com.vkr.tournament_service.entity.team.TournamentTeam;
import com.vkr.tournament_service.entity.tournament.Tournament;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamMapper {


    @Mapping(target = "tournamentId", source = "tournament.id")
    @Mapping(source = "players", target = "players")
    TeamDto toDto(TournamentTeam team);


    @Mapping(target = "tournament", ignore = true)
    @Mapping(target = "players", ignore = true)
    TournamentTeam toEntity(TeamCreateDto teamCreateDto);

    default TournamentTeam toEntity(TeamCreateDto teamCreateDto, UUID tournamentId) {
        TournamentTeam team = toEntity(teamCreateDto);
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        team.setTournament(tournament);
        return team;
    }

    TournamentTeam toEntity(TeamDto teamDto);
}
