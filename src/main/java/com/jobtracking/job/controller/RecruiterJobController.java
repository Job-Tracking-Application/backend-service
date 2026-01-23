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
@RequestMapping("/recruiter/jobs")
public class RecruiterJobController {

    private final JobService jobService;

    public RecruiterJobController(JobService jobService) {
        this.jobService = jobService;
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }

        return null;
    }

    @GetMapping("/my-jobs")
    public ResponseEntity<ApiResponse<List<Job>>> getMyJobs() {
        try {
            Long recruiterId = getCurrentUserId();
            if (recruiterId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "User not authenticated", null));
            }

            List<Job> jobs = jobService.getJobsByRecruiter(recruiterId);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Recruiter jobs fetched successfully", jobs)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error fetching recruiter jobs: " + e.getMessage(), null));
        }
    }
}