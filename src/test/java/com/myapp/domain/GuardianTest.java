package com.myapp.domain;

import static com.myapp.domain.GuardianTestSamples.*;
import static com.myapp.domain.PlayerTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class GuardianTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Guardian.class);
        Guardian guardian1 = getGuardianSample1();
        Guardian guardian2 = new Guardian();
        assertThat(guardian1).isNotEqualTo(guardian2);

        guardian2.setId(guardian1.getId());
        assertThat(guardian1).isEqualTo(guardian2);

        guardian2 = getGuardianSample2();
        assertThat(guardian1).isNotEqualTo(guardian2);
    }

    @Test
    void playersTest() {
        Guardian guardian = getGuardianRandomSampleGenerator();
        Player playerBack = getPlayerRandomSampleGenerator();

        guardian.addPlayers(playerBack);
        assertThat(guardian.getPlayers()).containsOnly(playerBack);
        assertThat(playerBack.getGuardians()).containsOnly(guardian);

        guardian.removePlayers(playerBack);
        assertThat(guardian.getPlayers()).doesNotContain(playerBack);
        assertThat(playerBack.getGuardians()).doesNotContain(guardian);

        guardian.players(new HashSet<>(Set.of(playerBack)));
        assertThat(guardian.getPlayers()).containsOnly(playerBack);
        assertThat(playerBack.getGuardians()).containsOnly(guardian);

        guardian.setPlayers(new HashSet<>());
        assertThat(guardian.getPlayers()).doesNotContain(playerBack);
        assertThat(playerBack.getGuardians()).doesNotContain(guardian);
    }
}
