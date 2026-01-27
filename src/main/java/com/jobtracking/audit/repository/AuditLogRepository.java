package com.jobtracking.audit.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jobtracking.audit.entity.AuditLog;
import com.jobtracking.common.repository.BaseRepository;

@Repository
public interface AuditLogRepository extends BaseRepository<AuditLog> {
    
    // Get logs ordered by most recent first
    List<AuditLog> findAllByOrderByPerformedAtDesc();
    
    // Find logs by entity type
    @Query("SELECT a FROM AuditLog a WHERE a.entity = :entity ORDER BY a.performedAt DESC")
    List<AuditLog> findByEntity(@Param("entity") String entity);
    
    // Find logs by entity and entity ID
    @Query("SELECT a FROM AuditLog a WHERE a.entity = :entity AND a.entityId = :entityId ORDER BY a.performedAt DESC")
    List<AuditLog> findByEntityAndEntityId(@Param("entity") String entity, @Param("entityId") Long entityId);
    
    // Find logs by performer
    @Query("SELECT a FROM AuditLog a WHERE a.performedBy = :performedBy ORDER BY a.performedAt DESC")
    List<AuditLog> findByPerformedBy(@Param("performedBy") Long performedBy);
    
    // Find logs by action
    @Query("SELECT a FROM AuditLog a WHERE a.action = :action ORDER BY a.performedAt DESC")
    List<AuditLog> findByAction(@Param("action") String action);
    
    // Find logs within date range
    @Query("SELECT a FROM AuditLog a WHERE a.performedAt BETWEEN :startDate AND :endDate ORDER BY a.performedAt DESC")
    List<AuditLog> findByPerformedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Find logs with pagination
    @Query("SELECT a FROM AuditLog a ORDER BY a.performedAt DESC")
    Page<AuditLog> findAllWithPagination(Pageable pageable);
    
    // Find logs by entity with pagination
    @Query("SELECT a FROM AuditLog a WHERE a.entity = :entity ORDER BY a.performedAt DESC")
    Page<AuditLog> findByEntityWithPagination(@Param("entity") String entity, Pageable pageable);
    
    // Count logs by entity
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.entity = :entity")
    long countByEntity(@Param("entity") String entity);
    
    // Count logs by performer
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.performedBy = :performedBy")
    long countByPerformedBy(@Param("performedBy") Long performedBy);
    
    // Delete old logs (cleanup)
    @Query("DELETE FROM AuditLog a WHERE a.performedAt < :cutoffDate")
    void deleteOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);
}