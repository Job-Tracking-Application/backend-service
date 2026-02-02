package com.jobtracking.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Secure user response that minimizes data exposure
 * Only includes essential information needed for frontend functionality
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecureUserResponse {
    // Essential for authorization
    private Integer roleId;
    private String role; // Human-readable role name
    
    // Essential for UI personalization (masked for privacy)
    private String displayName; // First name only or masked full name
    private String maskedEmail; // j***@example.com
    
    // Essential for functionality
    private Boolean active;
    private String languagePref;
    
    // Non-sensitive metadata
    private String accountType; // "PREMIUM", "BASIC", etc.
    
    // Security: What we DON'T expose
    // - Full email address
    // - Phone number  
    // - Username
    // - User ID
    // - Full name
    // - Creation dates
    // - Any sensitive_info
}