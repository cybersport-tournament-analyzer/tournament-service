package com.vkr.tournament_service.mapper.prediction;

import com.vkr.tournament_service.dto.prediction.BracketPredictionDto;
import com.vkr.tournament_service.entity.prediction.BracketPrediction;
import com.vkr.tournament_service.mapper.tournamentStage.TournamentStageMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring", uses = {TournamentStageMapper.class})
public interface BracketPredictionMapper {

    @Mapping(source = "id", target = "id", qualifiedByName = "uuidToString")
    BracketPredictionDto toDto(BracketPrediction prediction);

    @Mapping(source = "id", target = "id", qualifiedByName = "stringToUuid")
    BracketPrediction toEntity(BracketPredictionDto dto);

    @Named("uuidToString")
    static String uuidToString(UUID uuid) {
        return uuid != null ? uuid.toString() : null;
    }

    @Named("stringToUuid")
    static UUID stringToUuid(String id) {
        return id != null ? UUID.fromString(id) : null;
    }
}

