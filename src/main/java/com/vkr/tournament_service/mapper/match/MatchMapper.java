package com.vkr.tournament_service.mapper.match;

import com.vkr.tournament_service.dto.match.MatchCreateDto;
import com.vkr.tournament_service.dto.match.MatchDto;
import com.vkr.tournament_service.dto.match.MatchUpdateDto;
import com.vkr.tournament_service.entity.match.TournamentMatch;
import com.vkr.tournament_service.mapper.schedule.ScheduleMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = ScheduleMapper.class)
public interface MatchMapper {

    @Mapping(target = "tournament", ignore = true)
    @Mapping(target = "team1", ignore = true)
    @Mapping(target = "team2", ignore = true)
    @Mapping(target = "schedule", ignore = true)
    TournamentMatch toEntity(MatchCreateDto dto);

    @Mapping(target = "tournamentId", source = "tournament.id")
    @Mapping(target = "team1Name", source = "team1.teamName")
    @Mapping(target = "team2Name", source = "team2.teamName")
    @Mapping(target = "schedule", source = "schedule")
    MatchDto toDto(TournamentMatch entity);

    @Mapping(target = "tournament", ignore = true)
    @Mapping(target = "team1", ignore = true)
    @Mapping(target = "team2", ignore = true)
    @Mapping(target = "schedule", ignore = true)
    void updateEntity(MatchUpdateDto dto, @MappingTarget TournamentMatch entity);
}
