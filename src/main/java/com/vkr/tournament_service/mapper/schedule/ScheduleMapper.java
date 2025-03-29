package com.vkr.tournament_service.mapper.schedule;

import com.vkr.tournament_service.dto.schedule.ScheduleCreateDto;
import com.vkr.tournament_service.dto.schedule.ScheduleDto;
import com.vkr.tournament_service.dto.schedule.ScheduleUpdateDto;
import com.vkr.tournament_service.entity.schedule.TournamentSchedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ScheduleMapper {

    @Mapping(target = "match", ignore = true)
    TournamentSchedule toEntity(ScheduleCreateDto dto);

    @Mapping(target = "matchId", source = "match.id")
    ScheduleDto toDto(TournamentSchedule entity);

    @Mapping(target = "match", ignore = true)
    void updateEntity(ScheduleUpdateDto dto, @MappingTarget TournamentSchedule entity);
}
