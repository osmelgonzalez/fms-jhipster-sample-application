package com.myapp.service.mapper;

import com.myapp.domain.Player;
import com.myapp.domain.Team;
import com.myapp.service.dto.PlayerDTO;
import com.myapp.service.dto.TeamDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Team} and its DTO {@link TeamDTO}.
 */
@Mapper(componentModel = "spring")
public interface TeamMapper extends EntityMapper<TeamDTO, Team> {
    @Mapping(target = "players", source = "players", qualifiedByName = "playerIdSet")
    TeamDTO toDto(Team s);

    @Mapping(target = "removePlayers", ignore = true)
    Team toEntity(TeamDTO teamDTO);

    @Named("playerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PlayerDTO toDtoPlayerId(Player player);

    @Named("playerIdSet")
    default Set<PlayerDTO> toDtoPlayerIdSet(Set<Player> player) {
        return player.stream().map(this::toDtoPlayerId).collect(Collectors.toSet());
    }
}
