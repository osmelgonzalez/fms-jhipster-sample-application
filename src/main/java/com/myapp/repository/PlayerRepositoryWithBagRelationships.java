package com.myapp.repository;

import com.myapp.domain.Player;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface PlayerRepositoryWithBagRelationships {
    Optional<Player> fetchBagRelationships(Optional<Player> player);

    List<Player> fetchBagRelationships(List<Player> players);

    Page<Player> fetchBagRelationships(Page<Player> players);
}
