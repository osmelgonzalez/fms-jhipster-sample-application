package com.myapp.service.mapper;

import com.myapp.domain.Guardian;
import com.myapp.domain.Player;
import com.myapp.domain.Team;
import com.myapp.service.dto.GuardianDTO;
import com.myapp.service.dto.PlayerDTO;
import com.myapp.service.dto.TeamDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Player} and its DTO {@link PlayerDTO}.
 */
@Mapper(componentModel = "spring")
public interface PlayerMapper extends EntityMapper<PlayerDTO, Player> {
    @Mapping(target = "guardians", source = "guardians", qualifiedByName = "guardianIdSet")
    @Mapping(target = "teams", source = "teams", qualifiedByName = "teamIdSet")
    PlayerDTO toDto(Player s);

    @Mapping(target = "removeGuardians", ignore = true)
    @Mapping(target = "teams", ignore = true)
    @Mapping(target = "removeTeams", ignore = true)
    Player toEntity(PlayerDTO playerDTO);

    @Named("guardianId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    GuardianDTO toDtoGuardianId(Guardian guardian);

    @Named("guardianIdSet")
    default Set<GuardianDTO> toDtoGuardianIdSet(Set<Guardian> guardian) {
        return guardian.stream().map(this::toDtoGuardianId).collect(Collectors.toSet());
    }

    @Named("teamId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TeamDTO toDtoTeamId(Team team);

    @Named("teamIdSet")
    default Set<TeamDTO> toDtoTeamIdSet(Set<Team> team) {
        return team.stream().map(this::toDtoTeamId).collect(Collectors.toSet());
    }
}
