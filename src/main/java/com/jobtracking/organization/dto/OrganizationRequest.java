package com.jobtracking.organization.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OrganizationRequest(
        @NotBlank(message = "Company name is required")
        @Size(max = 255, message = "Company name must not exceed 255 characters")
        String name,

        @Size(max = 255, message = "Website URL must not exceed 255 characters")
        String website,

        @Size(max = 150, message = "City must not exceed 150 characters")
        String city,

        @Email(message = "Please provide a valid email address")
        @Size(max = 255, message = "Contact email must not exceed 255 characters")
        String contactEmail,

        String description,

        String extension
) {
}