package com.myapp.repository;

import com.myapp.domain.Player;
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
public class PlayerRepositoryWithBagRelationshipsImpl implements PlayerRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String PLAYERS_PARAMETER = "players";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Player> fetchBagRelationships(Optional<Player> player) {
        return player.map(this::fetchGuardians);
    }

    @Override
    public Page<Player> fetchBagRelationships(Page<Player> players) {
        return new PageImpl<>(fetchBagRelationships(players.getContent()), players.getPageable(), players.getTotalElements());
    }

    @Override
    public List<Player> fetchBagRelationships(List<Player> players) {
        return Optional.of(players).map(this::fetchGuardians).orElse(Collections.emptyList());
    }

    Player fetchGuardians(Player result) {
        return entityManager
            .createQuery("select player from Player player left join fetch player.guardians where player.id = :id", Player.class)
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<Player> fetchGuardians(List<Player> players) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, players.size()).forEach(index -> order.put(players.get(index).getId(), index));
        List<Player> result = entityManager
            .createQuery("select player from Player player left join fetch player.guardians where player in :players", Player.class)
            .setParameter(PLAYERS_PARAMETER, players)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
