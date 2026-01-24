package com.jobtracking.profile.dto;

public record RecruiterProfileResponse(
    String fullName,
    String email,
    String userName,
    String bio,
    String phone,
    String linkedinUrl,
    Integer yearsExperience,
    String specialization
) {}