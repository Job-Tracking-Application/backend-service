package com.jobtracking.application.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import com.jobtracking.application.dto.ApplicationResponse;
import com.jobtracking.application.dto.CandidateApplicationResponse;
import com.jobtracking.application.service.ApplicationService;
import com.jobtracking.common.controller.BaseController;
import com.jobtracking.common.response.ApiResponse;
import com.jobtracking.common.utils.AuthorizationUtil;
import com.jobtracking.common.utils.ResponseUtil;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import com.jobtracking.application.dto.UpdateStatusRequest;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import com.jobtracking.application.dto.ApplyJobRequest;

import jakarta.validation.Valid;

/**
 * Controller for managing job applications
 * Handles application creation, status updates, and retrieval
 */
@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
public class ApplicationController extends BaseController {
    private final ApplicationService applicationService;
    private final AuthorizationUtil authorizationUtil;

    @PostMapping("/{jobId}")
    public ResponseEntity<ApiResponse<String>> createApplication(@PathVariable Long jobId,
            @RequestBody ApplyJobRequest applyJobRequest, Authentication authentication) {
        try {
            Long userId = Long.valueOf(authentication.getName());
            applicationService.createApplication(jobId, userId, applyJobRequest);
            return ResponseUtil.success("Job applied successfully");
        } catch (IllegalStateException ex) {
            return ResponseUtil.conflict(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseUtil.error(ex.getMessage());
        } catch (Exception ex) {
            return ResponseUtil.internalError("Failed to create application: " + ex.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<CandidateApplicationResponse>>> getMyApplications(Authentication authentication) {
        try {
            Long userId = Long.valueOf(authentication.getName());
            return ResponseUtil.success(applicationService.getCandidateApplication(userId));
        } catch (Exception ex) {
            return ResponseUtil.internalError("Unable to fetch applications");
        }
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getApplicationsByJobId(@PathVariable Long jobId) {
        try {
            Long recruiterId = getCurrentUserId();
            if (recruiterId == null) {
                return ResponseUtil.unauthorized("User not authenticated");
            }

            // Use centralized authorization utility
            if (!authorizationUtil.isRecruiterAuthorizedForJob(recruiterId, jobId)) {
                return ResponseUtil.forbidden("You can only view applications for jobs from your verified organization");
            }

            List<ApplicationResponse> applications = applicationService.getApplication(jobId);
            if (applications.isEmpty()) {
                return ResponseUtil.noContent();
            }
            return ResponseUtil.success(applications);
        } catch (Exception ex) {
            return ResponseUtil.internalError("Unable to fetch applications for job: " + ex.getMessage());
        }
    }

    @PatchMapping("/manage/{id}")
    public ResponseEntity<ApiResponse<ApplicationResponse>> updateApplication(@PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest updateStatusRequest) {
        try {
            Long recruiterId = getCurrentUserId();
            if (recruiterId == null) {
                return ResponseUtil.unauthorized("User not authenticated");
            }

            // Use centralized authorization utility
            if (!authorizationUtil.isRecruiterAuthorizedForApplication(recruiterId, id)) {
                return ResponseUtil.forbidden("You can only manage applications for jobs from your verified organization");
            }

            ApplicationResponse response = applicationService.updateApplication(id, updateStatusRequest);
            return ResponseUtil.success(response, "Application status updated successfully");

        } catch (RuntimeException ex) {
            return ResponseUtil.notFound("Application not found with ID: " + id);
        } catch (Exception ex) {
            return ResponseUtil.internalError("Internal server error: " + ex.getMessage());
        }
    }

    @GetMapping("/check/{jobId}")
    public ResponseEntity<?> checkApplicationExists(@PathVariable Long jobId, Authentication authentication) {
        try {
            Long userId = Long.valueOf(authentication.getName());
            boolean hasApplied = applicationService.hasUserAppliedForJob(jobId, userId);
            return ResponseEntity.ok(java.util.Map.of("hasApplied", hasApplied));
        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unable to check application status");
        }
    }
}
