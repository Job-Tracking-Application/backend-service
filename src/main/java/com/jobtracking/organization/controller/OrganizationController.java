package com.jobtracking.organization.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobtracking.common.response.ApiResponse;
import com.jobtracking.organization.dto.OrganizationRequest;
import com.jobtracking.organization.dto.OrganizationResponse;
import com.jobtracking.organization.service.OrganizationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/organization")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? (Long) auth.getPrincipal() : null;
    }

    @PostMapping
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<OrganizationResponse>> createOrganization(
            @Valid @RequestBody OrganizationRequest request) {
        
        Long recruiterUserId = getCurrentUserId();
        OrganizationResponse response = organizationService.createOrganization(request, recruiterUserId);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Company profile created successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<OrganizationResponse>> updateOrganization(
            @PathVariable Long id,
            @Valid @RequestBody OrganizationRequest request) {
        
        Long recruiterUserId = getCurrentUserId();
        OrganizationResponse response = organizationService.updateOrganization(id, request, recruiterUserId);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Company profile updated successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrganizationResponse>> getOrganization(@PathVariable Long id) {
        OrganizationResponse response = organizationService.getOrganizationById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Company profile retrieved successfully", response));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<OrganizationResponse>> getMyOrganization() {
        Long recruiterUserId = getCurrentUserId();
        Optional<OrganizationResponse> response = organizationService.getRecruiterOrganization(recruiterUserId);
        
        if (response.isPresent()) {
            return ResponseEntity.ok(new ApiResponse<>(true, "Company profile retrieved successfully", response.get()));
        } else {
            return ResponseEntity.ok(new ApiResponse<>(true, "No company profile found", null));
        }
    }

    @GetMapping("/exists")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<Boolean>> hasOrganization() {
        Long recruiterUserId = getCurrentUserId();
        boolean exists = organizationService.hasOrganization(recruiterUserId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Organization existence checked", exists));
    }
}