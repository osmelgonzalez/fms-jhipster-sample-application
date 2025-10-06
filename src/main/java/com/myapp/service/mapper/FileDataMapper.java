package com.myapp.service.mapper;

import com.myapp.domain.FileData;
import com.myapp.service.dto.FileDataDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link FileData} and its DTO {@link FileDataDTO}.
 */
@Mapper(componentModel = "spring")
public interface FileDataMapper extends EntityMapper<FileDataDTO, FileData> {}
