package com.myapp.service.mapper;

import com.myapp.domain.FileData;
import com.myapp.domain.Tournament;
import com.myapp.service.dto.FileDataDTO;
import com.myapp.service.dto.TournamentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Tournament} and its DTO {@link TournamentDTO}.
 */
@Mapper(componentModel = "spring")
public interface TournamentMapper extends EntityMapper<TournamentDTO, Tournament> {
    @Mapping(target = "image", source = "image", qualifiedByName = "fileDataId")
    TournamentDTO toDto(Tournament s);

    @Named("fileDataId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    FileDataDTO toDtoFileDataId(FileData fileData);
}
