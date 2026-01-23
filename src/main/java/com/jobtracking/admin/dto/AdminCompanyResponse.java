package com.jobtracking.admin.dto;

import java.time.LocalDateTime;

public record AdminCompanyResponse(
        Long id,
        String name,
        String website,
        String city,
        String contactEmail,
        boolean verified,
        LocalDateTime createdAt) {
}
