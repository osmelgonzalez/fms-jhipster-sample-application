package com.myapp.domain;

import static com.myapp.domain.CampTestSamples.*;
import static com.myapp.domain.FileDataTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CampTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Camp.class);
        Camp camp1 = getCampSample1();
        Camp camp2 = new Camp();
        assertThat(camp1).isNotEqualTo(camp2);

        camp2.setId(camp1.getId());
        assertThat(camp1).isEqualTo(camp2);

        camp2 = getCampSample2();
        assertThat(camp1).isNotEqualTo(camp2);
    }

    @Test
    void imageTest() {
        Camp camp = getCampRandomSampleGenerator();
        FileData fileDataBack = getFileDataRandomSampleGenerator();

        camp.setImage(fileDataBack);
        assertThat(camp.getImage()).isEqualTo(fileDataBack);

        camp.image(null);
        assertThat(camp.getImage()).isNull();
    }
}
