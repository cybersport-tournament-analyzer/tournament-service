package com.vkr.tournament_service.mapper.tournament;

import com.vkr.tournament_service.dto.tournament.TournamentCreateDto;
import com.vkr.tournament_service.dto.tournament.TournamentDto;
import com.vkr.tournament_service.dto.tournament.TournamentUpdateDto;
import com.vkr.tournament_service.entity.tournament.Tournament;
import com.vkr.tournament_service.entity.tournament.TournamentStatus;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TournamentMapper {

    @Mapping(source = "tournamentStatus", target = "tournamentStatus", qualifiedByName = "getTournamentStatus")
    TournamentDto toDto(Tournament tournament);

    Tournament toEntity(TournamentCreateDto tournamentCreateDto);

    @Mapping(source = "tournamentStatus", target = "tournamentStatus", qualifiedByName = "getTournamentStatus")
    Tournament updateEntity(TournamentUpdateDto tournamentUpdateDto, @MappingTarget Tournament tournament);

    @Named("getTournamentStatus")
    default TournamentStatus getTournamentStatus(String tournamentStatus) {return TournamentStatus.fromString(tournamentStatus);};
}
