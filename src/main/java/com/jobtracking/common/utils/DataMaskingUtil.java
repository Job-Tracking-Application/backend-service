package com.jobtracking.common.utils;

/**
 * Utility class for masking sensitive user data
 * Helps protect user privacy while maintaining functionality
 */
public class DataMaskingUtil {
    
    /**
     * Masks email address for privacy
     * Example: john.doe@example.com -> j***@example.com
     */
    public static String maskEmail(String email) {
        if (email == null || email.isEmpty()) {
            return "";
        }
        
        String[] parts = email.split("@");
        if (parts.length != 2) {
            return "***@***.com"; // Invalid email format
        }
        
        String localPart = parts[0];
        String domain = parts[1];
        
        if (localPart.length() <= 1) {
            return localPart + "***@" + domain;
        }
        
        return localPart.charAt(0) + "***@" + domain;
    }
    
    /**
     * Creates display name from full name (first name only)
     * Example: "John Doe Smith" -> "John"
     */
    public static String createDisplayName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "User";
        }
        
        String[] nameParts = fullName.trim().split("\\s+");
        return nameParts[0]; // Return only first name
    }
    
    /**
     * Masks phone number for privacy
     * Example: +1234567890 -> +123***7890
     */
    public static String maskPhoneNumber(String phone) {
        if (phone == null || phone.length() < 4) {
            return "***";
        }
        
        if (phone.length() <= 6) {
            return phone.substring(0, 2) + "***";
        }
        
        return phone.substring(0, 3) + "***" + phone.substring(phone.length() - 4);
    }
    
    /**
     * Maps role ID to human-readable role name
     */
    public static String mapRoleIdToName(Integer roleId) {
        if (roleId == null) {
            return "USER";
        }
        
        switch (roleId) {
            case 1: return "ADMIN";
            case 2: return "RECRUITER";
            case 3: return "JOB_SEEKER";
            default: return "USER";
        }
    }
}