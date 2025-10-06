package com.myapp.domain;

import static com.myapp.domain.PlayerTestSamples.*;
import static com.myapp.domain.TeamTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TeamTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Team.class);
        Team team1 = getTeamSample1();
        Team team2 = new Team();
        assertThat(team1).isNotEqualTo(team2);

        team2.setId(team1.getId());
        assertThat(team1).isEqualTo(team2);

        team2 = getTeamSample2();
        assertThat(team1).isNotEqualTo(team2);
    }

    @Test
    void playersTest() {
        Team team = getTeamRandomSampleGenerator();
        Player playerBack = getPlayerRandomSampleGenerator();

        team.addPlayers(playerBack);
        assertThat(team.getPlayers()).containsOnly(playerBack);

        team.removePlayers(playerBack);
        assertThat(team.getPlayers()).doesNotContain(playerBack);

        team.players(new HashSet<>(Set.of(playerBack)));
        assertThat(team.getPlayers()).containsOnly(playerBack);

        team.setPlayers(new HashSet<>());
        assertThat(team.getPlayers()).doesNotContain(playerBack);
    }
}
