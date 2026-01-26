package com.jobtracking.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Separate DTO for profile management where full data is needed
 * Only accessible through dedicated profile endpoints with proper authorization
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDataResponse {
    private Long id;
    private String username;
    private String email;
    private String fullname;
    private String phone;
    private String languagePref;
    
    // Note: Still excludes sensitive fields like:
    // - passwordHash
    // - sensitiveInfo
    // - extension (if contains sensitive data)
}