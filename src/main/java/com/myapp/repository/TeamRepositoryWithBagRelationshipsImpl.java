package com.myapp.repository;

import com.myapp.domain.Team;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class TeamRepositoryWithBagRelationshipsImpl implements TeamRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String TEAMS_PARAMETER = "teams";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Team> fetchBagRelationships(Optional<Team> team) {
        return team.map(this::fetchPlayers);
    }

    @Override
    public Page<Team> fetchBagRelationships(Page<Team> teams) {
        return new PageImpl<>(fetchBagRelationships(teams.getContent()), teams.getPageable(), teams.getTotalElements());
    }

    @Override
    public List<Team> fetchBagRelationships(List<Team> teams) {
        return Optional.of(teams).map(this::fetchPlayers).orElse(Collections.emptyList());
    }

    Team fetchPlayers(Team result) {
        return entityManager
            .createQuery("select team from Team team left join fetch team.players where team.id = :id", Team.class)
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<Team> fetchPlayers(List<Team> teams) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, teams.size()).forEach(index -> order.put(teams.get(index).getId(), index));
        List<Team> result = entityManager
            .createQuery("select team from Team team left join fetch team.players where team in :teams", Team.class)
            .setParameter(TEAMS_PARAMETER, teams)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
