package com.vkr.tournament_service.mapper.tournament;

import com.vkr.tournament_service.dto.tournament.TournamentCreateDto;
import com.vkr.tournament_service.dto.tournament.TournamentDto;
import com.vkr.tournament_service.dto.tournament.TournamentUpdateDto;
import com.vkr.tournament_service.entity.tournament.Stage;
import com.vkr.tournament_service.entity.tournament.Tournament;
import com.vkr.tournament_service.entity.tournament.TournamentStatus;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TournamentMapper {

    @Mapping(source = "tournamentStatus", target = "tournamentStatus", qualifiedByName = "getTournamentStatus")
    @Mapping(source = "stages", target = "stages", qualifiedByName = "mapStagesToStringList")
    TournamentDto toDto(Tournament tournament);

    @Mapping(source = "stages", target = "stages", qualifiedByName = "mapStageNamesToStages")
    Tournament toEntity(TournamentCreateDto tournamentCreateDto);

    @Mapping(source = "tournamentStatus", target = "tournamentStatus", qualifiedByName = "getTournamentStatus")
    @Mapping(source = "stages", target = "stages", qualifiedByName = "mapStageNamesToStages")
    Tournament updateEntity(TournamentUpdateDto tournamentUpdateDto, @MappingTarget Tournament tournament);

    @Named("getTournamentStatus")
    default TournamentStatus getTournamentStatus(String tournamentStatus) {
        return TournamentStatus.fromString(tournamentStatus);
    }

    @Named("mapStageNamesToStages")
    default List<Stage> mapStageNamesToStages(List<String> stageNames) {
        return stageNames.stream()
                .map(Stage::fromName)
                .collect(Collectors.toList());
    }

    @Named("mapStagesToStringList")
    default List<String> mapStagesToStringList(List<Stage> stages) {
        return stages.stream()
                .map(Stage::getName)
                .collect(Collectors.toList());
    }
}