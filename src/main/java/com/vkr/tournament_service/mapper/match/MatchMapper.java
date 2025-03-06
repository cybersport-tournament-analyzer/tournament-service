package com.vkr.tournament_service.mapper.match;

import com.vkr.tournament_service.dto.match.MatchCreateDto;
import com.vkr.tournament_service.dto.match.MatchDto;
import com.vkr.tournament_service.entity.match.TournamentMatch;
import com.vkr.tournament_service.entity.tournament.Tournament;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MatchMapper {

    @Mapping(target = "tournamentId", source = "tournament.id")
    @Mapping(source = "matches", target = "matches")
    MatchDto toDto(TournamentMatch tournamentMatch);

    @Mapping(target = "tournament", ignore = true)
    @Mapping(target = "matches", ignore = true)
    TournamentMatch toEntity(MatchCreateDto matchCreateDto);

    default TournamentMatch toEntity(MatchCreateDto matchCreateDto, UUID tournamentId) {
        TournamentMatch match = toEntity(matchCreateDto);
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        match.setTournament(tournament);
        return match;
    }
}
