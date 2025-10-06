package com.myapp.repository;

import com.myapp.domain.FileData;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the FileData entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FileDataRepository extends JpaRepository<FileData, Long> {}
