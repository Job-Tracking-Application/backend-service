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
public class ApplicationController {
    private final ApplicationService applicationService;

    @PostMapping("/{jobId}")
    public ResponseEntity<String> createApplication(@PathVariable Long jobId,
            @RequestBody ApplyJobRequest applyJobRequest, Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        applicationService.createApplication(jobId, userId, applyJobRequest);
        return ResponseEntity.ok("Job applied successfully");
    }

    @GetMapping("/me")
    public ResponseEntity<List<CandidateApplicationResponse>> getMyApplications(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(applicationService.getCandidateApplication(userId));
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsByJobId(@PathVariable Long jobId) {
        List<ApplicationResponse> applications = applicationService.getApplication(jobId);
        if (applications.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .build();
        }
        return ResponseEntity.ok(applications);
    }

    @PatchMapping("/manage/{id}")
    public ResponseEntity<ApplicationResponse> updateApplication(@PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest updateStatusRequest) {
        ApplicationResponse response = applicationService.updateApplication(id, updateStatusRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check/{jobId}")
    public ResponseEntity<java.util.Map<String, Boolean>> checkApplicationExists(@PathVariable Long jobId,
            Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        boolean hasApplied = applicationService.hasUserAppliedForJob(jobId, userId);
        return ResponseEntity.ok(java.util.Map.of("hasApplied", hasApplied));
    }
}
