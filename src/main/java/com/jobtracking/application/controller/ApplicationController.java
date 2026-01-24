package com.jobtracking.application.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import com.jobtracking.application.dto.ApplicationResponse;
import com.jobtracking.application.service.ApplicationService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import com.jobtracking.application.dto.UpdateStatusRequest;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import com.jobtracking.application.dto.ApplyJobRequest;

@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService applicationService;

    @PostMapping("/{jobId}")
    public ResponseEntity<String> createApplication(@PathVariable Long jobId,
            @RequestBody ApplyJobRequest applyJobRequest, Authentication authentication) {
        try {
            Long userId = Long.valueOf(authentication.getPrincipal().toString());
            applicationService.createApplication(jobId, userId,
                    applyJobRequest); 
            return ResponseEntity.ok("Job applied successfully");
        } catch (IllegalStateException ex) {
            // duplicate application
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(ex.getMessage());
        } catch (IllegalArgumentException ex) {        // user or job not found
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ex.getMessage());
        }
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyApplications(Authentication authentication) {
        try {
            Long userId = Long.valueOf(authentication.getPrincipal().toString());
            return ResponseEntity.ok(
                    applicationService.getCandidateApplication(userId)
            );
        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unable to fetch applications");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyApplicationsAlias(Authentication authentication) {
        try {
            Long userId = Long.valueOf(authentication.getPrincipal().toString());
            return ResponseEntity.ok(
                    applicationService.getCandidateApplication(userId)
            );
        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unable to fetch applications");
        }
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<?> getApplicationsByJobId(@PathVariable Long jobId) {
        try {
            List<ApplicationResponse> applications = applicationService.getApplication(jobId);
            if (applications.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NO_CONTENT)
                        .build();
            }
            return ResponseEntity.ok(applications);
        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unable to fetch applications for job: " + ex.getMessage());
        }
    }

    @PatchMapping("/manage/{id}")
    public ResponseEntity<?> updateApplication(@PathVariable Long id,
            @RequestBody UpdateStatusRequest updateStatusRequest) {
    	 try {
    	        ApplicationResponse response =
    	                applicationService.updateApplication(id, updateStatusRequest);
    	        return ResponseEntity.ok(response);

    	    } catch (RuntimeException ex) {
    	        return ResponseEntity
    	                .status(HttpStatus.NOT_FOUND)
    	                .body(ex.getMessage());
    	    }
    }

    @GetMapping("/check/{jobId}")
    public ResponseEntity<?> checkApplicationExists(@PathVariable Long jobId, Authentication authentication) {
        try {
            Long userId = Long.valueOf(authentication.getPrincipal().toString());
            boolean hasApplied = applicationService.hasUserAppliedForJob(jobId, userId);
            return ResponseEntity.ok(java.util.Map.of("hasApplied", hasApplied));
        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unable to check application status");
        }
    }
   

}
