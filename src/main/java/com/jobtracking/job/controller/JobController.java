package com.jobtracking.job.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.jobtracking.common.response.ApiResponse;
import com.jobtracking.job.dto.JobWithSkillsResponse;
import com.jobtracking.job.entity.Job;
import com.jobtracking.job.service.JobService;
import com.jobtracking.organization.repository.OrganizationRepository;

@RestController
@RequestMapping("/jobs")
public class JobController {

    private final JobService jobService;
    private final OrganizationRepository organizationRepository;

    public JobController(JobService jobService, OrganizationRepository organizationRepository) {
        this.jobService = jobService;
        this.organizationRepository = organizationRepository;
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        // The principal is the userId (String) set in JwtAuthenticationFilter
        Object principal = auth.getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        } else if (principal instanceof String) {
            try {
                return Long.parseLong((String) principal);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return null;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<JobWithSkillsResponse>> createJob(
            @RequestBody Job job,
            @RequestParam(required = false) List<Long> skillIds) {

        try {
            Long recruiterId = getCurrentUserId();
            if (recruiterId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "User not authenticated", null));
            }

            // Validate that the recruiter owns the company
            if (!isRecruiterCompanyValid(recruiterId, job.getCompanyId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>(false, "You can only create jobs for your own company", null));
            }

            job.setRecruiterUserId(recruiterId);

            if (skillIds == null) {
                skillIds = List.of();
            }

            Job savedJob = jobService.createJob(job, skillIds);
            
            // Convert to DTO to avoid circular reference
            JobWithSkillsResponse response = jobService.getJobWithSkillsById(savedJob.getId());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Job created successfully", response));
                    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error creating job: " + e.getMessage(), null));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<JobWithSkillsResponse>>> getAllJobs() {
        try {
            List<JobWithSkillsResponse> jobs = jobService.getAllJobsWithSkills();
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Jobs fetched successfully", jobs)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error fetching jobs: " + e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobWithSkillsResponse>> getJob(@PathVariable Long id) {
        try {
            JobWithSkillsResponse job = jobService.getJobWithSkillsById(id);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Job fetched successfully", job)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Job not found: " + e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<JobWithSkillsResponse>> updateJob(
            @PathVariable Long id,
            @RequestBody Job job,
            @RequestParam(required = false) List<Long> skillIds) {

        try {
            Long recruiterId = getCurrentUserId();
            if (recruiterId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "User not authenticated", null));
            }

            // Validate that the recruiter owns the company
            if (!isRecruiterCompanyValid(recruiterId, job.getCompanyId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>(false, "You can only update jobs for your own company", null));
            }

            if (skillIds == null) {
                skillIds = List.of();
            }

            Job updatedJob = jobService.updateJob(id, job, skillIds);
            
            // Convert to DTO to avoid circular reference
            JobWithSkillsResponse response = jobService.getJobWithSkillsById(updatedJob.getId());

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Job updated successfully", response)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error updating job: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteJob(@PathVariable Long id) {
        try {
            jobService.deleteJob(id);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Job deleted successfully", null)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error deleting job: " + e.getMessage(), null));
        }
    }

    /**
     * Validates that the recruiter owns the company they're trying to create/update a job for
     */
    private boolean isRecruiterCompanyValid(Long recruiterId, Long companyId) {
        if (companyId == null) {
            return false;
        }
        
        return organizationRepository.findById(companyId)
                .map(org -> org.getRecruiterUserId().equals(recruiterId))
                .orElse(false);
    }
}