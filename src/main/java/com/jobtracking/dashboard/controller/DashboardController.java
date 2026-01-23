package com.jobtracking.dashboard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobtracking.common.response.ApiResponse;
import com.jobtracking.dashboard.dto.DashboardStatsResponse;
import com.jobtracking.dashboard.service.DashboardService;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        // The principal is the userId (Long) set in JwtAuthenticationFilter
        Object principal = auth.getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }

        return null;
    }

    @GetMapping("/recruiter/stats")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getRecruiterStats() {
        try {
            Long recruiterId = getCurrentUserId();
            if (recruiterId == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "User not authenticated", null));
            }
            
            DashboardStatsResponse stats = dashboardService.getRecruiterStats(recruiterId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Recruiter stats retrieved successfully", stats));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Error retrieving recruiter stats: " + e.getMessage(), null));
        }
    }

    @GetMapping("/jobseeker/stats")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getJobSeekerStats() {
        try {
            Long jobSeekerId = getCurrentUserId();
            if (jobSeekerId == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "User not authenticated", null));
            }
            
            DashboardStatsResponse stats = dashboardService.getJobSeekerStats(jobSeekerId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Job seeker stats retrieved successfully", stats));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Error retrieving job seeker stats: " + e.getMessage(), null));
        }
    }
}