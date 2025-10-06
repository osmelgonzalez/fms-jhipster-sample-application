package com.myapp.service.mapper;

import static com.myapp.domain.GuardianAsserts.*;
import static com.myapp.domain.GuardianTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GuardianMapperTest {

    private GuardianMapper guardianMapper;

    @BeforeEach
    void setUp() {
        guardianMapper = new GuardianMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getGuardianSample1();
        var actual = guardianMapper.toEntity(guardianMapper.toDto(expected));
        assertGuardianAllPropertiesEquals(expected, actual);
    }
}
