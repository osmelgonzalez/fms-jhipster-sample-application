package com.myapp.domain;

import static com.myapp.domain.CheckinTestSamples.*;
import static com.myapp.domain.PlayerTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CheckinTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Checkin.class);
        Checkin checkin1 = getCheckinSample1();
        Checkin checkin2 = new Checkin();
        assertThat(checkin1).isNotEqualTo(checkin2);

        checkin2.setId(checkin1.getId());
        assertThat(checkin1).isEqualTo(checkin2);

        checkin2 = getCheckinSample2();
        assertThat(checkin1).isNotEqualTo(checkin2);
    }

    @Test
    void playerTest() {
        Checkin checkin = getCheckinRandomSampleGenerator();
        Player playerBack = getPlayerRandomSampleGenerator();

        checkin.setPlayer(playerBack);
        assertThat(checkin.getPlayer()).isEqualTo(playerBack);

        checkin.player(null);
        assertThat(checkin.getPlayer()).isNull();
    }
}
