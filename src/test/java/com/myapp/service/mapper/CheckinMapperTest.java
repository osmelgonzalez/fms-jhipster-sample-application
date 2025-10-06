package com.myapp.service.mapper;

import static com.myapp.domain.CheckinAsserts.*;
import static com.myapp.domain.CheckinTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CheckinMapperTest {

    private CheckinMapper checkinMapper;

    @BeforeEach
    void setUp() {
        checkinMapper = new CheckinMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCheckinSample1();
        var actual = checkinMapper.toEntity(checkinMapper.toDto(expected));
        assertCheckinAllPropertiesEquals(expected, actual);
    }
}
