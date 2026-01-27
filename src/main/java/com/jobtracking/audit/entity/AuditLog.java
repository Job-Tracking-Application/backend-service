package com.jobtracking.audit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import com.jobtracking.common.entity.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "logs")
@Getter
@Setter
public class AuditLog extends BaseEntity {

	private String entity;
	private Long entityId;
	private String action;
	private Long performedBy;

	private LocalDateTime performedAt;

	@Column(columnDefinition = "json")
	private String extension;
}