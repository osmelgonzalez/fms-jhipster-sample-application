package com.myapp.service.mapper;

import static com.myapp.domain.TournamentAsserts.*;
import static com.myapp.domain.TournamentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TournamentMapperTest {

    private TournamentMapper tournamentMapper;

    @BeforeEach
    void setUp() {
        tournamentMapper = new TournamentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTournamentSample1();
        var actual = tournamentMapper.toEntity(tournamentMapper.toDto(expected));
        assertTournamentAllPropertiesEquals(expected, actual);
    }
}
