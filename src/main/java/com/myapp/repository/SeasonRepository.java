package com.myapp.repository;

import com.myapp.domain.Season;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Season entity.
 */
@Repository
public interface SeasonRepository extends JpaRepository<Season, Long> {
    default Optional<Season> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Season> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Season> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select season from Season season left join fetch season.organization",
        countQuery = "select count(season) from Season season"
    )
    Page<Season> findAllWithToOneRelationships(Pageable pageable);

    @Query("select season from Season season left join fetch season.organization")
    List<Season> findAllWithToOneRelationships();

    @Query("select season from Season season left join fetch season.organization where season.id =:id")
    Optional<Season> findOneWithToOneRelationships(@Param("id") Long id);
}
