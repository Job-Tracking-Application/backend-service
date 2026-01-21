package com.jobtracking.audit.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.jobtracking.audit.entity.AuditLog;
import com.jobtracking.audit.repository.AuditLogRepository;

@Service
public class AuditLogService {
	private final AuditLogRepository repo;

	public AuditLogService(AuditLogRepository repo) {
		this.repo = repo;
	}

	public void log(String entity, Long entityId, String action, Long userId) {
		AuditLog log = new AuditLog();
		log.setEntity(entity);
		log.setEntityId(entityId);
		log.setAction(action);
		log.setPerformedBy(userId);
		log.setPerformedAt(LocalDateTime.now());
		repo.save(log);
	}

	public List<AuditLog> findAll() {
		return repo.findAll();
	}
}