package com.myapp.domain;

import static com.myapp.domain.FileDataTestSamples.*;
import static com.myapp.domain.TournamentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TournamentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Tournament.class);
        Tournament tournament1 = getTournamentSample1();
        Tournament tournament2 = new Tournament();
        assertThat(tournament1).isNotEqualTo(tournament2);

        tournament2.setId(tournament1.getId());
        assertThat(tournament1).isEqualTo(tournament2);

        tournament2 = getTournamentSample2();
        assertThat(tournament1).isNotEqualTo(tournament2);
    }

    @Test
    void imageTest() {
        Tournament tournament = getTournamentRandomSampleGenerator();
        FileData fileDataBack = getFileDataRandomSampleGenerator();

        tournament.setImage(fileDataBack);
        assertThat(tournament.getImage()).isEqualTo(fileDataBack);

        tournament.image(null);
        assertThat(tournament.getImage()).isNull();
    }
}
