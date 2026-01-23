package com.jobtracking.organization.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.jobtracking.organization.entity.Organization;
import com.jobtracking.organization.repository.OrganizationRepository;
import com.jobtracking.organization.service.OrganizationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/organizations")
public class OrganizationController {

    private static final Logger log = LoggerFactory.getLogger(OrganizationController.class);

    private final OrganizationService organizationService;
    private final OrganizationRepository organizationRepository;

    public OrganizationController(
            OrganizationService organizationService,
            OrganizationRepository organizationRepository) {
        this.organizationService = organizationService;
        this.organizationRepository = organizationRepository;
    }

    /**
     * SAFELY extract logged-in user id from JWT authentication
     */
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


    // ===================== GET ALL ORGANIZATIONS =====================
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrganizationResponse>>> getAllOrganizations() {
        try {
            List<OrganizationResponse> response = organizationRepository.findAll()
                    .stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Organizations retrieved successfully", response)
            );
        } catch (Exception e) {
            log.error("Error fetching organizations", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error retrieving organizations", null));
        }
    }

    // ===================== TEST ENDPOINT =====================
    @GetMapping("/test")
    public ResponseEntity<ApiResponse<String>> testConnection() {
        try {
            long count = organizationRepository.count();
            return ResponseEntity.ok(
                    new ApiResponse<>(true,
                            "Database connection working. Organizations count: " + count,
                            "OK")
            );
        } catch (Exception e) {
            log.error("Database test failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Database error", null));
        }
    }

    // ===================== TEST JOB ENDPOINT =====================
    @GetMapping("/test-job-endpoint")
    public ResponseEntity<ApiResponse<String>> testJobEndpoint() {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Job endpoint test from OrganizationController", "OK")
        );
    }

    // ===================== GET BY ID =====================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrganizationResponse>> getOrganization(@PathVariable Long id) {
        return organizationRepository.findById(id)
                .map(org -> ResponseEntity.ok(
                        new ApiResponse<>(true, "Organization retrieved successfully", mapToResponse(org))
                ))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Organization not found", null)));
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

        OrganizationResponse response =
                organizationService.createOrganization(request, recruiterUserId);

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

        OrganizationResponse response =
                organizationService.updateOrganization(id, request, recruiterUserId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Organization updated successfully", response)
        );
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

        Optional<OrganizationResponse> response =
                organizationService.getRecruiterOrganization(recruiterUserId);

        return ResponseEntity.ok(
                new ApiResponse<>(true,
                        response.isPresent() ? "Organization retrieved successfully" : "No organization found",
                        response.orElse(null))
        );
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
                new ApiResponse<>(true, "Organization existence checked", exists)
        );
    }

    // ===================== MAPPER =====================
    private OrganizationResponse mapToResponse(Organization org) {
        return new OrganizationResponse(
                org.getId(),
                org.getName(),
                org.getWebsite(),
                org.getCity(),
                org.getContactEmail(),
                org.getDescription(),
                org.getVerified(),
                org.getCreatedAt(),
                org.getUpdatedAt(),
                org.getExtension()
        );
    }
}