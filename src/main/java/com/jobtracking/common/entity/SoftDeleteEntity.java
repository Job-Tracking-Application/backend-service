package com.jobtracking.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Base entity for entities that support soft delete
 */
@MappedSuperclass
@Getter
@Setter
public abstract class SoftDeleteEntity extends BaseEntity {

    @Column(name = "deleted_at")
    protected LocalDateTime deletedAt;

    /**
     * Check if entity is soft deleted
     */
    public boolean isDeleted() {
        return deletedAt != null;
    }

    /**
     * Mark entity as deleted (soft delete)
     */
    public void markAsDeleted() {
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * Restore soft deleted entity
     */
    public void restore() {
        this.deletedAt = null;
    }
}