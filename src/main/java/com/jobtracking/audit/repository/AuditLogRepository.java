package com.jobtracking.audit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobtracking.audit.entity.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long>{
	
	// Get logs ordered by most recent first
	List<AuditLog> findAllByOrderByPerformedAtDesc();
}