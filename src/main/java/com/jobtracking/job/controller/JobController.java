package com.jobtracking.job.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.jobtracking.common.response.ApiResponse;
import com.jobtracking.job.entity.Job;
import com.jobtracking.job.service.JobService;

@RestController
@RequestMapping("/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
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
                System.err.println("Error parsing userId from principal: " + principal);
                return null;
            }
        }

        return null;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Job>> createJob(
            @RequestBody Job job,
            @RequestParam(required = false) List<Long> skillIds) {

        try {
            Long recruiterId = getCurrentUserId();
            if (recruiterId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "User not authenticated", null));
            }

            job.setRecruiterUserId(recruiterId);

            if (skillIds == null) {
                skillIds = List.of();
            }

            Job savedJob = jobService.createJob(job, skillIds);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Job created successfully", savedJob));
                    
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error creating job: " + e.getMessage(), null));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Job>>> getAllJobs() {
        try {
            List<Job> jobs = jobService.getAllJobs();
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Jobs fetched successfully", jobs)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error fetching jobs: " + e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Job>> getJob(@PathVariable Long id) {
        try {
            Job job = jobService.getJobById(id);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Job fetched successfully", job)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Job not found: " + e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Job>> updateJob(
            @PathVariable Long id,
            @RequestBody Job job,
            @RequestParam(required = false) List<Long> skillIds) {

        try {
            Long recruiterId = getCurrentUserId();
            if (recruiterId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "User not authenticated", null));
            }

            if (skillIds == null) {
                skillIds = List.of();
            }

            Job updatedJob = jobService.updateJob(id, job, skillIds);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Job updated successfully", updatedJob)
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
}