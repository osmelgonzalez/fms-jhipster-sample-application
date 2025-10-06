package com.myapp.domain;

import static com.myapp.domain.FileDataTestSamples.*;
import static com.myapp.domain.OrganizationTestSamples.*;
import static com.myapp.domain.SeasonTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SeasonTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Season.class);
        Season season1 = getSeasonSample1();
        Season season2 = new Season();
        assertThat(season1).isNotEqualTo(season2);

        season2.setId(season1.getId());
        assertThat(season1).isEqualTo(season2);

        season2 = getSeasonSample2();
        assertThat(season1).isNotEqualTo(season2);
    }

    @Test
    void imageTest() {
        Season season = getSeasonRandomSampleGenerator();
        FileData fileDataBack = getFileDataRandomSampleGenerator();

        season.setImage(fileDataBack);
        assertThat(season.getImage()).isEqualTo(fileDataBack);

        season.image(null);
        assertThat(season.getImage()).isNull();
    }

    @Test
    void organizationTest() {
        Season season = getSeasonRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        season.setOrganization(organizationBack);
        assertThat(season.getOrganization()).isEqualTo(organizationBack);

        season.organization(null);
        assertThat(season.getOrganization()).isNull();
    }
}
