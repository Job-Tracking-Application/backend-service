package com.jobtracking.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateStatusRequest(
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "APPLIED|UNDER_REVIEW|INTERVIEWED|SHORTLISTED|REJECTED|HIRED|PENDING", 
             message = "Status must be one of: APPLIED, UNDER_REVIEW, INTERVIEWED, SHORTLISTED, REJECTED, HIRED, PENDING")
    String status
) {
}