package com.myapp.service.mapper;

import com.myapp.domain.FileData;
import com.myapp.domain.Organization;
import com.myapp.domain.Season;
import com.myapp.service.dto.FileDataDTO;
import com.myapp.service.dto.OrganizationDTO;
import com.myapp.service.dto.SeasonDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Season} and its DTO {@link SeasonDTO}.
 */
@Mapper(componentModel = "spring")
public interface SeasonMapper extends EntityMapper<SeasonDTO, Season> {
    @Mapping(target = "image", source = "image", qualifiedByName = "fileDataId")
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    SeasonDTO toDto(Season s);

    @Named("fileDataId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    FileDataDTO toDtoFileDataId(FileData fileData);

    @Named("organizationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OrganizationDTO toDtoOrganizationName(Organization organization);
}
