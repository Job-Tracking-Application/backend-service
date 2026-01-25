
package com.jobtracking.application.dto;

public record ApplyJobRequest(
        String resume, // optional (URL or path)
        String coverLetter,
        String portfolioUrl, // optional portfolio URL
        String linkedinUrl, // optional LinkedIn profile URL
        String githubUrl, // optional GitHub profile URL
        String additionalNotes) { // optional additional notes

}