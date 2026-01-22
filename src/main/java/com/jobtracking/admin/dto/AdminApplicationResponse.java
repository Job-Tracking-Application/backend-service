package com.jobtracking.admin.dto;

import java.time.LocalDateTime;

public record AdminApplicationResponse(
        Long id,
        Long jobId,
        String jobTitle,
        Long jobSeekerUserId,
        String jobSeekerName,
        String status,
        LocalDateTime appliedAt,
        LocalDateTime updatedAt,
        String resumePath) {
}
