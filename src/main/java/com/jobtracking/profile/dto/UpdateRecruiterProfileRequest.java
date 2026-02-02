package com.jobtracking.profile.dto;

public record UpdateRecruiterProfileRequest(
    String fullName,
    String bio,
    String phone,
    String linkedinUrl,
    Integer yearsExperience,
    String specialization
) {}