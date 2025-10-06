package com.myapp.service.mapper;

import static com.myapp.domain.FileDataAsserts.*;
import static com.myapp.domain.FileDataTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FileDataMapperTest {

    private FileDataMapper fileDataMapper;

    @BeforeEach
    void setUp() {
        fileDataMapper = new FileDataMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getFileDataSample1();
        var actual = fileDataMapper.toEntity(fileDataMapper.toDto(expected));
        assertFileDataAllPropertiesEquals(expected, actual);
    }
}
