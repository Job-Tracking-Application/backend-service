package com.jobtracking.application.dto;

import java.util.List;

public record ApplicationResponse(
        Long id,
        String name,
        String email,
        List<String> skills,
        String status,
        String resume,
        String coverLetter,
        String portfolioUrl,
        String linkedinUrl,
        String githubUrl,
        String additionalNotes) {
}