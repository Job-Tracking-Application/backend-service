package com.jobtracking.audit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobtracking.audit.entity.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long>{
	
}