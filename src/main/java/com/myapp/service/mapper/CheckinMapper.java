package com.myapp.service.mapper;

import com.myapp.domain.Checkin;
import com.myapp.domain.Player;
import com.myapp.service.dto.CheckinDTO;
import com.myapp.service.dto.PlayerDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Checkin} and its DTO {@link CheckinDTO}.
 */
@Mapper(componentModel = "spring")
public interface CheckinMapper extends EntityMapper<CheckinDTO, Checkin> {
    @Mapping(target = "player", source = "player", qualifiedByName = "playerId")
    CheckinDTO toDto(Checkin s);

    @Named("playerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PlayerDTO toDtoPlayerId(Player player);
}
