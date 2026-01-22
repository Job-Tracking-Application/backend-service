package com.jobtracking.admin.dto;

import java.time.LocalDateTime;

public record AdminUserResponse(
		Long id,
		String username,
		String email,
		String role,
		Boolean active,
		LocalDateTime createdAt

) {
};