package com.myapp.service.mapper;

import static com.myapp.domain.CampAsserts.*;
import static com.myapp.domain.CampTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CampMapperTest {

    private CampMapper campMapper;

    @BeforeEach
    void setUp() {
        campMapper = new CampMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCampSample1();
        var actual = campMapper.toEntity(campMapper.toDto(expected));
        assertCampAllPropertiesEquals(expected, actual);
    }
}
