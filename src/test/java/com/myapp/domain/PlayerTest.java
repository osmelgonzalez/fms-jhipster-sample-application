package com.myapp.domain;

import static com.myapp.domain.CheckinTestSamples.*;
import static com.myapp.domain.GuardianTestSamples.*;
import static com.myapp.domain.PlayerTestSamples.*;
import static com.myapp.domain.TeamTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PlayerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Player.class);
        Player player1 = getPlayerSample1();
        Player player2 = new Player();
        assertThat(player1).isNotEqualTo(player2);

        player2.setId(player1.getId());
        assertThat(player1).isEqualTo(player2);

        player2 = getPlayerSample2();
        assertThat(player1).isNotEqualTo(player2);
    }

    @Test
    void checkinsTest() {
        Player player = getPlayerRandomSampleGenerator();
        Checkin checkinBack = getCheckinRandomSampleGenerator();

        player.addCheckins(checkinBack);
        assertThat(player.getCheckins()).containsOnly(checkinBack);
        assertThat(checkinBack.getPlayer()).isEqualTo(player);

        player.removeCheckins(checkinBack);
        assertThat(player.getCheckins()).doesNotContain(checkinBack);
        assertThat(checkinBack.getPlayer()).isNull();

        player.checkins(new HashSet<>(Set.of(checkinBack)));
        assertThat(player.getCheckins()).containsOnly(checkinBack);
        assertThat(checkinBack.getPlayer()).isEqualTo(player);

        player.setCheckins(new HashSet<>());
        assertThat(player.getCheckins()).doesNotContain(checkinBack);
        assertThat(checkinBack.getPlayer()).isNull();
    }

    @Test
    void guardiansTest() {
        Player player = getPlayerRandomSampleGenerator();
        Guardian guardianBack = getGuardianRandomSampleGenerator();

        player.addGuardians(guardianBack);
        assertThat(player.getGuardians()).containsOnly(guardianBack);

        player.removeGuardians(guardianBack);
        assertThat(player.getGuardians()).doesNotContain(guardianBack);

        player.guardians(new HashSet<>(Set.of(guardianBack)));
        assertThat(player.getGuardians()).containsOnly(guardianBack);

        player.setGuardians(new HashSet<>());
        assertThat(player.getGuardians()).doesNotContain(guardianBack);
    }

    @Test
    void teamsTest() {
        Player player = getPlayerRandomSampleGenerator();
        Team teamBack = getTeamRandomSampleGenerator();

        player.addTeams(teamBack);
        assertThat(player.getTeams()).containsOnly(teamBack);
        assertThat(teamBack.getPlayers()).containsOnly(player);

        player.removeTeams(teamBack);
        assertThat(player.getTeams()).doesNotContain(teamBack);
        assertThat(teamBack.getPlayers()).doesNotContain(player);

        player.teams(new HashSet<>(Set.of(teamBack)));
        assertThat(player.getTeams()).containsOnly(teamBack);
        assertThat(teamBack.getPlayers()).containsOnly(player);

        player.setTeams(new HashSet<>());
        assertThat(player.getTeams()).doesNotContain(teamBack);
        assertThat(teamBack.getPlayers()).doesNotContain(player);
    }
}
