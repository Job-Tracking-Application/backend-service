package com.jobtracking.admin.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.jobtracking.admin.dto.AdminApplicationResponse;
import com.jobtracking.application.entity.Application;
import com.jobtracking.application.enums.ApplicationStatus;
import com.jobtracking.common.exception.AdminException;
import com.jobtracking.common.exception.EntityNotFoundException;
import com.jobtracking.common.exception.ValidationException;
import com.jobtracking.application.repository.ApplicationRepository;
import com.jobtracking.audit.service.AuditLogService;
import com.jobtracking.auth.entity.User;
import com.jobtracking.auth.repository.UserRepository;
import com.jobtracking.job.entity.Job;
import com.jobtracking.job.repository.JobRepository;
import com.jobtracking.common.utils.ValidationUtil;

import lombok.RequiredArgsConstructor;

/**
 * Service for admin application management operations
 * Follows Single Responsibility Principle - only handles application-related admin operations
 */
@Service
@RequiredArgsConstructor
public class AdminApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    /**
     * Get paginated applications with optional status filter
     */
    public Page<AdminApplicationResponse> getAllApplications(int page, int size, String status) {
        ValidationUtil.validateRange(page, 0, Integer.MAX_VALUE, "Page must be non-negative");
        ValidationUtil.validateRange(size, 1, 100, "Size must be between 1 and 100");

        Pageable pageable = PageRequest.of(page, size, Sort.by("appliedAt").descending());
        
        // Convert string status to enum if provided
        ApplicationStatus statusEnum = null;
        if (status != null && !status.trim().isEmpty()) {
            try {
                statusEnum = ApplicationStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ValidationException("status", "Invalid application status: " + status);
            }
        }
        
        Page<Application> applications = applicationRepository.filterApplications(statusEnum, pageable);
        
        // Create lookup maps for efficient data retrieval
        Map<Long, String> jobTitles = createJobTitlesMap();
        Map<Long, String> userNames = createUserNamesMap();

        return applications.map(application -> mapToAdminResponse(application, jobTitles, userNames));
    }

    /**
     * Get single application by ID
     */
    public AdminApplicationResponse getApplicationById(Long applicationId) {
        ValidationUtil.validateNotNull(applicationId, "Application ID cannot be null");

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found with ID: " + applicationId));
        
        // Check if application is soft deleted
        if (application.getDeletedAt() != null) {
            throw new AdminException("Application has been deleted");
        }
        
        Map<Long, String> jobTitles = createJobTitlesMap();
        Map<Long, String> userNames = createUserNamesMap();

        return mapToAdminResponse(application, jobTitles, userNames);
    }

    /**
     * Soft delete an application
     */
    public void deleteApplication(Long applicationId, Long adminId) {
        ValidationUtil.validateNotNull(applicationId, "Application ID cannot be null");
        ValidationUtil.validateNotNull(adminId, "Admin ID cannot be null");

        applicationRepository.findById(applicationId).ifPresentOrElse(application -> {
            if (application.getDeletedAt() != null) {
                throw new AdminException("Application is already deleted");
            }
            
            // Soft delete: set deletedAt timestamp
            application.setDeletedAt(LocalDateTime.now());
            applicationRepository.save(application);
            
            auditLogService.log("APPLICATION", applicationId, "ADMIN_DELETE", adminId,
                "Application for job '" + application.getJob().getTitle() + "' deleted by admin");
        }, () -> {
            throw new EntityNotFoundException("Application", applicationId);
        });
    }

    /**
     * Get application count for statistics
     */
    public long getApplicationCount() {
        return applicationRepository.count();
    }

    /**
     * Get application count by status
     */
    public long getApplicationCountByStatus(ApplicationStatus status) {
        ValidationUtil.validateNotNull(status, "Status cannot be null");
        return applicationRepository.countByStatus(status);
    }

    /**
     * Create job titles lookup map
     */
    private Map<Long, String> createJobTitlesMap() {
        return jobRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Job::getId,
                        Job::getTitle,
                        (existing, replacement) -> existing // Handle duplicate keys
                ));
    }

    /**
     * Create user names lookup map
     */
    private Map<Long, String> createUserNamesMap() {
        return userRepository.findAll().stream()
                .collect(Collectors.toMap(
                        User::getId,
                        User::getUsername,
                        (existing, replacement) -> existing // Handle duplicate keys
                ));
    }

    /**
     * Map Application entity to AdminApplicationResponse
     */
    private AdminApplicationResponse mapToAdminResponse(Application application, 
            Map<Long, String> jobTitles, Map<Long, String> userNames) {
        return new AdminApplicationResponse(
                application.getId(),
                application.getJob().getId(),
                jobTitles.getOrDefault(application.getJob().getId(), "Unknown Job"),
                application.getUser().getId(),
                userNames.getOrDefault(application.getUser().getId(), "Unknown User"),
                application.getStatus().name(),
                application.getAppliedAt(),
                application.getUpdatedAt(),
                application.getResumePath());
    }
}