package com.myapp.service.mapper;

import com.myapp.domain.Guardian;
import com.myapp.domain.Player;
import com.myapp.service.dto.GuardianDTO;
import com.myapp.service.dto.PlayerDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Guardian} and its DTO {@link GuardianDTO}.
 */
@Mapper(componentModel = "spring")
public interface GuardianMapper extends EntityMapper<GuardianDTO, Guardian> {
    @Mapping(target = "players", source = "players", qualifiedByName = "playerIdSet")
    GuardianDTO toDto(Guardian s);

    @Mapping(target = "players", ignore = true)
    @Mapping(target = "removePlayers", ignore = true)
    Guardian toEntity(GuardianDTO guardianDTO);

    @Named("playerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PlayerDTO toDtoPlayerId(Player player);

    @Named("playerIdSet")
    default Set<PlayerDTO> toDtoPlayerIdSet(Set<Player> player) {
        return player.stream().map(this::toDtoPlayerId).collect(Collectors.toSet());
    }
}
