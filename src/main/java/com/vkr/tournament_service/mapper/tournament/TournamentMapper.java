package com.vkr.tournament_service.mapper.tournament;

import com.vkr.tournament_service.dto.tournament.TournamentCreateDto;
import com.vkr.tournament_service.dto.tournament.TournamentDto;
import com.vkr.tournament_service.dto.tournament.TournamentUpdateDto;
import com.vkr.tournament_service.entity.tournament.Tournament;
import com.vkr.tournament_service.entity.tournament.TournamentStatus;
import com.vkr.tournament_service.mapper.match.MatchMapper;
import com.vkr.tournament_service.mapper.team.TeamMapper;
import com.vkr.tournament_service.mapper.tournamentStage.TournamentStageMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        uses = {TournamentStageMapper.class, MatchMapper.class, TeamMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TournamentMapper {

    @Mapping(target = "stages", source = "stages")
    @Mapping(target = "matches", source = "matches")
    @Mapping(target = "teams", source = "teams")
    TournamentDto toDto(Tournament entity);

    @Mapping(target = "stages", source = "stages")
    @Mapping(target = "tournamentStatus", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "matches", ignore = true)
    @Mapping(target = "teams", ignore = true)
    Tournament toEntity(TournamentCreateDto dto);

    @Mapping(target = "stages", ignore = true)
    @Mapping(target = "matches", ignore = true)
    @Mapping(target = "teams", ignore = true)
    void updateEntity(TournamentUpdateDto dto, @MappingTarget Tournament entity);

    default TournamentStatus mapStatus(String status) {
        return TournamentStatus.valueOf(status);
    }
}