package com.jobtracking.organization.controller;

import java.util.List;
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
@RequestMapping("/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    /**
     * SAFELY extract logged-in user id from JWT authentication
     */
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

    // ===================== GET ALL ORGANIZATIONS =====================
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrganizationResponse>>> getAllOrganizations() {
        List<OrganizationResponse> response = organizationService.getAllOrganizations();
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Organizations retrieved successfully", response));
    }

    // ===================== GET RECRUITER'S ORGANIZATIONS =====================
    @GetMapping("/recruiter")
    public ResponseEntity<ApiResponse<Optional<OrganizationResponse>>> getRecruiterOrganizations() {
        Long recruiterId = getCurrentUserId();
        if (recruiterId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "User not authenticated", null));
        }

        Optional<OrganizationResponse> response = organizationService.getRecruiterOrganization(recruiterId);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Recruiter organizations retrieved successfully", response));
    }

    // ===================== GET BY ID =====================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrganizationResponse>> getOrganization(@PathVariable Long id) {
        OrganizationResponse response = organizationService.getOrganizationById(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Organization retrieved successfully", response));
    }

    // ===================== CREATE ORGANIZATION =====================
    @PostMapping
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<OrganizationResponse>> createOrganization(
            @Valid @RequestBody OrganizationRequest request) {

        Long recruiterUserId = getCurrentUserId();
        if (recruiterUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "User not authenticated", null));
        }

        OrganizationResponse response = organizationService.createOrganization(request, recruiterUserId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Organization created successfully", response));
    }

    // ===================== UPDATE ORGANIZATION =====================
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<OrganizationResponse>> updateOrganization(
            @PathVariable Long id,
            @Valid @RequestBody OrganizationRequest request) {

        Long recruiterUserId = getCurrentUserId();
        if (recruiterUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "User not authenticated", null));
        }

        OrganizationResponse response = organizationService.updateOrganization(id, request, recruiterUserId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Organization updated successfully", response));
    }

    // ===================== GET MY ORGANIZATION =====================
    @GetMapping("/my")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<OrganizationResponse>> getMyOrganization() {

        Long recruiterUserId = getCurrentUserId();
        if (recruiterUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "User not authenticated", null));
        }

        Optional<OrganizationResponse> response = organizationService.getRecruiterOrganization(recruiterUserId);

        return ResponseEntity.ok(
                new ApiResponse<>(true,
                        response.isPresent() ? "Organization retrieved successfully" : "No organization found",
                        response.orElse(null)));
    }

    // ===================== CHECK ORGANIZATION EXISTS =====================
    @GetMapping("/exists")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<Boolean>> hasOrganization() {

        Long recruiterUserId = getCurrentUserId();
        if (recruiterUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "User not authenticated", false));
        }

        boolean exists = organizationService.hasOrganization(recruiterUserId);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Organization existence checked", exists));
    }

}
