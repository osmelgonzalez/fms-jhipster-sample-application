package com.myapp.service.mapper;

import com.myapp.domain.Camp;
import com.myapp.domain.FileData;
import com.myapp.service.dto.CampDTO;
import com.myapp.service.dto.FileDataDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Camp} and its DTO {@link CampDTO}.
 */
@Mapper(componentModel = "spring")
public interface CampMapper extends EntityMapper<CampDTO, Camp> {
    @Mapping(target = "image", source = "image", qualifiedByName = "fileDataId")
    CampDTO toDto(Camp s);

    @Named("fileDataId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    FileDataDTO toDtoFileDataId(FileData fileData);
}
