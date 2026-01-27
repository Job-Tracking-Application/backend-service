package com.jobtracking.admin.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jobtracking.admin.dto.AdminJobResponse;
import com.jobtracking.audit.service.AuditLogService;
import com.jobtracking.job.repository.JobRepository;
import com.jobtracking.organization.entity.Organization;
import com.jobtracking.organization.repository.OrganizationRepository;
import com.jobtracking.common.utils.ValidationUtil;

import lombok.RequiredArgsConstructor;

/**
 * Service for admin job management operations
 * Follows Single Responsibility Principle - only handles job-related admin operations
 */
@Service
@RequiredArgsConstructor
public class AdminJobService {

    private final JobRepository jobRepository;
    private final OrganizationRepository organizationRepository;
    private final AuditLogService auditLogService;

    /**
     * Get all jobs for admin view
     */
    public List<AdminJobResponse> getAllJobs() {
        Map<Long, String> companyNames = getCompanyNamesMap();
        
        return jobRepository.findAll().stream()
                .filter(job -> job.getDeletedAt() == null) // Only active jobs
                .map(job -> new AdminJobResponse(
                        job.getId(),
                        job.getTitle(),
                        companyNames.getOrDefault(job.getCompanyId(), "Unknown Company"),
                        job.getIsActive(),
                        job.getCreatedAt()))
                .toList();
    }

    /**
     * Soft delete a job
     */
    public void deleteJob(Long jobId, Long adminId) {
        ValidationUtil.validateNotNull(jobId, "Job ID cannot be null");
        ValidationUtil.validateNotNull(adminId, "Admin ID cannot be null");

        jobRepository.findById(jobId).ifPresentOrElse(job -> {
            if (job.getDeletedAt() != null) {
                throw new IllegalStateException("Job is already deleted");
            }
            
            job.setDeletedAt(LocalDateTime.now());
            jobRepository.save(job);
            
            auditLogService.log("JOB", jobId, "ADMIN_DELETE", adminId,
                "Job '" + job.getTitle() + "' deleted by admin");
        }, () -> {
            throw new IllegalArgumentException("Job not found with ID: " + jobId);
        });
    }

    /**
     * Toggle job active status (verify/unverify)
     */
    public void toggleJobStatus(Long jobId, Long adminId) {
        ValidationUtil.validateNotNull(jobId, "Job ID cannot be null");
        ValidationUtil.validateNotNull(adminId, "Admin ID cannot be null");

        jobRepository.findById(jobId).ifPresentOrElse(job -> {
            if (job.getDeletedAt() != null) {
                throw new IllegalStateException("Cannot modify deleted job");
            }
            
            boolean newStatus = !job.getIsActive();
            job.setIsActive(newStatus);
            jobRepository.save(job);
            
            String action = newStatus ? "ACTIVATED" : "DEACTIVATED";
            auditLogService.log("JOB", jobId, action, adminId,
                "Job '" + job.getTitle() + "' " + action.toLowerCase() + " by admin");
        }, () -> {
            throw new IllegalArgumentException("Job not found with ID: " + jobId);
        });
    }

    /**
     * Get job count for statistics
     */
    public long getJobCount() {
        return jobRepository.count();
    }

    /**
     * Get active job count
     */
    public long getActiveJobCount() {
        return jobRepository.countByIsActiveTrueAndDeletedAtIsNull();
    }

    /**
     * Get company names map for job display
     */
    private Map<Long, String> getCompanyNamesMap() {
        try {
            return organizationRepository.findAll().stream()
                    .collect(Collectors.toMap(
                            Organization::getId,
                            Organization::getName,
                            (existing, replacement) -> existing // Handle duplicate keys
                    ));
        } catch (Exception e) {
            // Return empty map if companies can't be loaded
            return new HashMap<>();
        }
    }
}