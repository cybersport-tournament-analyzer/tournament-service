package com.vkr.tournament_service.mapper.tournamentStage;

import com.vkr.tournament_service.dto.tournamentStage.TournamentStageDto;
import com.vkr.tournament_service.dto.tournamentStage.TournamentStageUpdateDto;
import com.vkr.tournament_service.entity.match.TournamentMatch;
import com.vkr.tournament_service.entity.tournamentStage.Stage;
import com.vkr.tournament_service.entity.tournamentStage.TournamentStage;
import com.vkr.tournament_service.mapper.match.MatchMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        uses = {MatchMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TournamentStageMapper {

    @Mapping(target = "tournamentId", source = "tournament.id")
    @Mapping(target = "stageType", source = "stageType.name")
    @Mapping(target = "matches", source = "matches")
    TournamentStageDto toDto(TournamentStage entity);

    @Mapping(target = "tournament", ignore = true)
    @Mapping(target = "matches", ignore = true)
    void updateEntity(TournamentStageUpdateDto dto, @MappingTarget TournamentStage entity);

    default Stage mapStageType(String stageType) {
        return Stage.fromName(stageType);
    }

    default List<UUID> mapMatches(List<TournamentMatch> matches) {
        return matches.stream()
                .map(TournamentMatch::getId)
                .collect(Collectors.toList());
    }
}
