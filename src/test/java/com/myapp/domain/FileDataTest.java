package com.myapp.domain;

import static com.myapp.domain.CampTestSamples.*;
import static com.myapp.domain.FileDataTestSamples.*;
import static com.myapp.domain.SeasonTestSamples.*;
import static com.myapp.domain.TournamentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FileDataTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FileData.class);
        FileData fileData1 = getFileDataSample1();
        FileData fileData2 = new FileData();
        assertThat(fileData1).isNotEqualTo(fileData2);

        fileData2.setId(fileData1.getId());
        assertThat(fileData1).isEqualTo(fileData2);

        fileData2 = getFileDataSample2();
        assertThat(fileData1).isNotEqualTo(fileData2);
    }

    @Test
    void tournamentTest() {
        FileData fileData = getFileDataRandomSampleGenerator();
        Tournament tournamentBack = getTournamentRandomSampleGenerator();

        fileData.setTournament(tournamentBack);
        assertThat(fileData.getTournament()).isEqualTo(tournamentBack);
        assertThat(tournamentBack.getImage()).isEqualTo(fileData);

        fileData.tournament(null);
        assertThat(fileData.getTournament()).isNull();
        assertThat(tournamentBack.getImage()).isNull();
    }

    @Test
    void seasonTest() {
        FileData fileData = getFileDataRandomSampleGenerator();
        Season seasonBack = getSeasonRandomSampleGenerator();

        fileData.setSeason(seasonBack);
        assertThat(fileData.getSeason()).isEqualTo(seasonBack);
        assertThat(seasonBack.getImage()).isEqualTo(fileData);

        fileData.season(null);
        assertThat(fileData.getSeason()).isNull();
        assertThat(seasonBack.getImage()).isNull();
    }

    @Test
    void campTest() {
        FileData fileData = getFileDataRandomSampleGenerator();
        Camp campBack = getCampRandomSampleGenerator();

        fileData.setCamp(campBack);
        assertThat(fileData.getCamp()).isEqualTo(campBack);
        assertThat(campBack.getImage()).isEqualTo(fileData);

        fileData.camp(null);
        assertThat(fileData.getCamp()).isNull();
        assertThat(campBack.getImage()).isNull();
    }
}
