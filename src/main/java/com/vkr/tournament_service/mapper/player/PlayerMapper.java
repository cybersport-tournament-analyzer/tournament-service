package com.vkr.tournament_service.mapper.player;

import com.vkr.tournament_service.dto.player.PlayerCreateDto;
import com.vkr.tournament_service.dto.player.PlayerDto;
import com.vkr.tournament_service.entity.player.Player;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PlayerMapper {

    PlayerDto toDto(Player player);
    
    Player toEntity(PlayerCreateDto tournamentCreateDto);

}
