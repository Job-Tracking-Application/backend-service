package com.jobtracking.common.utils;

import org.springframework.stereotype.Component;

import com.jobtracking.common.service.VerificationService;
import com.jobtracking.job.repository.JobRepository;
import com.jobtracking.application.repository.ApplicationRepository;

import lombok.RequiredArgsConstructor;

/**
 * Centralized authorization utility to eliminate duplicate authorization checks
 * Follows DRY principle and provides consistent authorization logic
 */
@Component
@RequiredArgsConstructor
public class AuthorizationUtil {

    private final VerificationService verificationService;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    /**
     * Check if recruiter is authorized for organization (owns + verified)
     */
    public boolean isRecruiterAuthorizedForOrganization(Long recruiterId, Long organizationId) {
        return verificationService.isRecruiterAuthorizedForOrganization(recruiterId, organizationId);
    }

    /**
     * Check if recruiter is authorized for job (owns job + verified organization)
     */
    public boolean isRecruiterAuthorizedForJob(Long recruiterId, Long jobId) {
        return jobRepository.findById(jobId)
                .map(job -> job.getRecruiterUserId().equals(recruiterId) && 
                           verificationService.isOrganizationVerified(job.getCompanyId()))
                .orElse(false);
    }

    /**
     * Check if recruiter is authorized for application (owns job + verified organization)
     */
    public boolean isRecruiterAuthorizedForApplication(Long recruiterId, Long applicationId) {
        return applicationRepository.findById(applicationId)
                .map(app -> isRecruiterAuthorizedForJob(recruiterId, app.getJob().getId()))
                .orElse(false);
    }

    /**
     * Check if user owns the application
     */
    public boolean isUserAuthorizedForApplication(Long userId, Long applicationId) {
        return applicationRepository.findById(applicationId)
                .map(app -> app.getUser().getId().equals(userId))
                .orElse(false);
    }

    /**
     * Check if recruiter owns the job
     */
    public boolean isRecruiterOwnerOfJob(Long recruiterId, Long jobId) {
        return jobRepository.findById(jobId)
                .map(job -> job.getRecruiterUserId().equals(recruiterId))
                .orElse(false);
    }
}