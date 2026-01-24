package com.jobtracking.profile.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record UpdateProfileRequest(
                @NotBlank(message = "Full name is required")
                @Size(max = 255, message = "Full name must not exceed 255 characters")
                String fullName,
                
                @Email(message = "Please provide a valid email address")
                @Size(max = 255, message = "Email must not exceed 255 characters")
                String email,
                
                List<String> skills,
                
                @Size(max = 500, message = "Resume URL must not exceed 500 characters")
                String resume,
                
                @Size(max = 1000, message = "About section must not exceed 1000 characters")
                String about,
                
                @Size(max = 500, message = "Education must not exceed 500 characters")
                String education) {
}
