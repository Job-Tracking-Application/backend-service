package com.jobtracking.audit.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jobtracking.audit.entity.AuditLog;
import com.jobtracking.audit.repository.AuditLogRepository;

@Service
public class AuditLogService {
	private final AuditLogRepository repo;
	private final ObjectMapper objectMapper;

	public AuditLogService(AuditLogRepository repo, ObjectMapper objectMapper) {
		this.repo = repo;
		this.objectMapper = objectMapper;
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

	// Overloaded method for actions without specific entity ID
	public void log(String entity, String action, Long userId) {
		log(entity, null, action, userId);
	}

	// Method for logging with additional details
	public void log(String entity, Long entityId, String action, Long userId, String details) {
		AuditLog log = new AuditLog();
		log.setEntity(entity);
		log.setEntityId(entityId);
		log.setAction(action);
		log.setPerformedBy(userId);
		log.setPerformedAt(LocalDateTime.now());
		
		// Convert details to proper JSON format using ObjectMapper
		if (details != null && !details.trim().isEmpty()) {
			try {
				ObjectNode jsonNode = objectMapper.createObjectNode();
				jsonNode.put("details", details);
				log.setExtension(objectMapper.writeValueAsString(jsonNode));
			} catch (Exception e) {
				// Fallback to simple JSON if ObjectMapper fails
				log.setExtension("{\"details\":\"Error serializing details\"}");
			}
		}
		
		repo.save(log);
	}

	public List<AuditLog> findAll() {
		return repo.findAll();
	}
}