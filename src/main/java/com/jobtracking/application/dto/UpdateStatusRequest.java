package com.jobtracking.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateStatusRequest(
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "APPLIED|SHORTLISTED|REJECTED|HIRED", 
             message = "Status must be one of: APPLIED, SHORTLISTED, REJECTED, HIRED")
    String status
) {
}