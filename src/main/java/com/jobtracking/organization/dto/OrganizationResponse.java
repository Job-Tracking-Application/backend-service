package com.jobtracking.organization.dto;

import java.time.LocalDateTime;

public record OrganizationResponse(
        Long id,
        String name,
        String website,
        String city,
        String contactEmail,
        String description,
        Boolean verified,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String extension
) {
}