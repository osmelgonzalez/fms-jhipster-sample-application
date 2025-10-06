package com.myapp.repository;

import com.myapp.domain.Guardian;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Guardian entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GuardianRepository extends JpaRepository<Guardian, Long> {}
