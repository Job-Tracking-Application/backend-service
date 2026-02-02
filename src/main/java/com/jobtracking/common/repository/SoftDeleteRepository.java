package com.jobtracking.common.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import com.jobtracking.common.entity.SoftDeleteEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for entities that support soft delete
 * Extends BaseRepository with soft delete specific methods
 */
@NoRepositoryBean
public interface SoftDeleteRepository<T extends SoftDeleteEntity> extends BaseRepository<T> {

    /**
     * Find all non-deleted entities
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.deletedAt IS NULL ORDER BY e.createdAt DESC")
    List<T> findAllActive();

    /**
     * Find all non-deleted entities ordered by update date
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.deletedAt IS NULL ORDER BY e.updatedAt DESC")
    List<T> findAllActiveOrderByUpdatedAt();

    /**
     * Find non-deleted entity by ID
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.id = :id AND e.deletedAt IS NULL")
    Optional<T> findByIdAndNotDeleted(@Param("id") Long id);

    /**
     * Check if non-deleted entity exists by ID
     */
    @Query("SELECT COUNT(e) > 0 FROM #{#entityName} e WHERE e.id = :id AND e.deletedAt IS NULL")
    boolean existsByIdAndNotDeleted(@Param("id") Long id);

    /**
     * Count all non-deleted entities
     */
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.deletedAt IS NULL")
    long countActive();

    /**
     * Count all deleted entities
     */
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.deletedAt IS NOT NULL")
    long countDeleted();

    /**
     * Soft delete entity by ID
     */
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.deletedAt = :deletedAt WHERE e.id = :id AND e.deletedAt IS NULL")
    int softDeleteById(@Param("id") Long id, @Param("deletedAt") LocalDateTime deletedAt);

    /**
     * Restore soft deleted entity by ID
     */
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.deletedAt = NULL WHERE e.id = :id AND e.deletedAt IS NOT NULL")
    int restoreById(@Param("id") Long id);

    /**
     * Find all deleted entities
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.deletedAt IS NOT NULL ORDER BY e.deletedAt DESC")
    List<T> findAllDeleted();

    /**
     * Permanently delete all soft deleted entities older than specified date
     */
    @Modifying
    @Query("DELETE FROM #{#entityName} e WHERE e.deletedAt IS NOT NULL AND e.deletedAt < :cutoffDate")
    int permanentlyDeleteOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);
}