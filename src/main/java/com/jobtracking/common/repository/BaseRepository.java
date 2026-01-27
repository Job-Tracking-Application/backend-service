package com.jobtracking.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import com.jobtracking.common.entity.BaseEntity;

import java.util.List;
import java.util.Optional;

/**
 * Base repository interface providing common query methods for all entities
 * Uses @NoRepositoryBean to prevent Spring from creating an implementation
 */
@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity> extends JpaRepository<T, Long> {

    /**
     * Find all entities ordered by creation date (newest first)
     */
    @Query("SELECT e FROM #{#entityName} e ORDER BY e.createdAt DESC")
    List<T> findAllOrderByCreatedAtDesc();

    /**
     * Find all entities ordered by update date (most recently updated first)
     */
    @Query("SELECT e FROM #{#entityName} e ORDER BY e.updatedAt DESC")
    List<T> findAllOrderByUpdatedAtDesc();

    /**
     * Find entity by ID with null check
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.id = :id")
    Optional<T> findByIdSafe(@Param("id") Long id);

    /**
     * Check if entity exists by ID
     */
    @Query("SELECT COUNT(e) > 0 FROM #{#entityName} e WHERE e.id = :id")
    boolean existsByIdSafe(@Param("id") Long id);

    /**
     * Count all entities
     */
    @Query("SELECT COUNT(e) FROM #{#entityName} e")
    long countAll();
}