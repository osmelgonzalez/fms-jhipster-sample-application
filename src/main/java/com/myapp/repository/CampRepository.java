package com.myapp.repository;

import com.myapp.domain.Camp;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Camp entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CampRepository extends JpaRepository<Camp, Long> {}
