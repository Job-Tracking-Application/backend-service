package com.jobtracking.common.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.jobtracking.common.entity.BaseEntity;
import com.jobtracking.common.entity.SoftDeleteEntity;
import com.jobtracking.common.exception.EntityNotFoundException;
import com.jobtracking.common.repository.BaseRepository;
import com.jobtracking.common.repository.SoftDeleteRepository;
import com.jobtracking.common.utils.ValidationUtil;

import java.util.List;

/**
 * Service providing common repository operations
 * Reduces boilerplate code in service classes
 */
@Service
public class RepositoryService {

    /**
     * Find entity by ID or throw exception
     */
    public <T extends BaseEntity> T findByIdOrThrow(BaseRepository<T> repository, Long id, String entityName) {
        ValidationUtil.validateNotNull(id, entityName + " ID cannot be null");
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityName + " not found with ID: " + id));
    }

    /**
     * Find active entity by ID or throw exception (for soft delete entities)
     */
    public <T extends SoftDeleteEntity> T findActiveByIdOrThrow(SoftDeleteRepository<T> repository, Long id, String entityName) {
        ValidationUtil.validateNotNull(id, entityName + " ID cannot be null");
        return repository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new EntityNotFoundException(entityName + " not found with ID: " + id));
    }

    /**
     * Check if entity exists by ID
     */
    public <T extends BaseEntity> boolean existsById(BaseRepository<T> repository, Long id) {
        if (id == null) return false;
        return repository.existsByIdSafe(id);
    }

    /**
     * Check if active entity exists by ID (for soft delete entities)
     */
    public <T extends SoftDeleteEntity> boolean existsActiveById(SoftDeleteRepository<T> repository, Long id) {
        if (id == null) return false;
        return repository.existsByIdAndNotDeleted(id);
    }

    /**
     * Get paginated results with default sorting
     */
    public <T extends BaseEntity> Page<T> findPaginated(BaseRepository<T> repository, int page, int size, String sortBy, String sortDirection) {
        ValidationUtil.validateRange(page, 0, Integer.MAX_VALUE, "Page must be non-negative");
        ValidationUtil.validateRange(size, 1, 100, "Size must be between 1 and 100");
        
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        return repository.findAll(pageable);
    }

    /**
     * Get paginated active results (for soft delete entities)
     */
    public <T extends SoftDeleteEntity> List<T> findActivePaginated(SoftDeleteRepository<T> repository, int page, int size) {
        ValidationUtil.validateRange(page, 0, Integer.MAX_VALUE, "Page must be non-negative");
        ValidationUtil.validateRange(size, 1, 100, "Size must be between 1 and 100");
        
        List<T> allActive = repository.findAllActive();
        int start = page * size;
        int end = Math.min(start + size, allActive.size());
        
        if (start >= allActive.size()) {
            return List.of();
        }
        
        return allActive.subList(start, end);
    }

    /**
     * Soft delete entity by ID
     */
    public <T extends SoftDeleteEntity> void softDelete(SoftDeleteRepository<T> repository, Long id, String entityName) {
        T entity = findActiveByIdOrThrow(repository, id, entityName);
        entity.markAsDeleted();
        repository.save(entity);
    }

    /**
     * Restore soft deleted entity by ID
     */
    public <T extends SoftDeleteEntity> void restore(SoftDeleteRepository<T> repository, Long id, String entityName) {
        ValidationUtil.validateNotNull(id, entityName + " ID cannot be null");
        
        T entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityName + " not found with ID: " + id));
        
        if (!entity.isDeleted()) {
            throw new IllegalStateException(entityName + " is not deleted");
        }
        
        entity.restore();
        repository.save(entity);
    }

    /**
     * Save entity with validation
     */
    public <T extends BaseEntity> T save(BaseRepository<T> repository, T entity, String entityName) {
        ValidationUtil.validateNotNull(entity, entityName + " cannot be null");
        return repository.save(entity);
    }

    /**
     * Save all entities with validation
     */
    public <T extends BaseEntity> List<T> saveAll(BaseRepository<T> repository, List<T> entities, String entityName) {
        ValidationUtil.validateNotNull(entities, entityName + " list cannot be null");
        ValidationUtil.validateNotEmpty(entities, entityName + " list cannot be empty");
        return repository.saveAll(entities);
    }

    /**
     * Delete entity by ID (hard delete)
     */
    public <T extends BaseEntity> void deleteById(BaseRepository<T> repository, Long id, String entityName) {
        if (!existsById(repository, id)) {
            throw new EntityNotFoundException(entityName + " not found with ID: " + id);
        }
        repository.deleteById(id);
    }

    /**
     * Get entity statistics
     */
    public <T extends BaseEntity> EntityStats getStats(BaseRepository<T> repository) {
        long total = repository.countAll();
        return new EntityStats(total);
    }

    /**
     * Get soft delete entity statistics
     */
    public <T extends SoftDeleteEntity> SoftDeleteEntityStats getSoftDeleteStats(SoftDeleteRepository<T> repository) {
        long total = repository.countAll();
        long active = repository.countActive();
        long deleted = repository.countDeleted();
        return new SoftDeleteEntityStats(total, active, deleted);
    }

    /**
     * Entity statistics holder
     */
    public static class EntityStats {
        private final long total;

        public EntityStats(long total) {
            this.total = total;
        }

        public long getTotal() {
            return total;
        }
    }

    /**
     * Soft delete entity statistics holder
     */
    public static class SoftDeleteEntityStats extends EntityStats {
        private final long active;
        private final long deleted;

        public SoftDeleteEntityStats(long total, long active, long deleted) {
            super(total);
            this.active = active;
            this.deleted = deleted;
        }

        public long getActive() {
            return active;
        }

        public long getDeleted() {
            return deleted;
        }
    }
}