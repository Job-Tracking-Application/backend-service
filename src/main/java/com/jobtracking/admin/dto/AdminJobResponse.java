package com.jobtracking.admin.dto;

import java.time.LocalDateTime;

public record AdminJobResponse(
    Long id,
    String title,
    String companyName,
    String status,
    LocalDateTime createdAt
) {}
