package com.jobtracking.organization.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.jobtracking.audit.service.AuditLogService;
import com.jobtracking.common.exception.CustomException;
import com.jobtracking.organization.dto.OrganizationRequest;
import com.jobtracking.organization.dto.OrganizationResponse;
import com.jobtracking.organization.entity.Organization;
import com.jobtracking.organization.repository.OrganizationRepository;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final AuditLogService auditLogService;

    public OrganizationService(OrganizationRepository organizationRepository, AuditLogService auditLogService) {
        this.organizationRepository = organizationRepository;
        this.auditLogService = auditLogService;
    }

    public OrganizationResponse createOrganization(OrganizationRequest request, Long recruiterUserId) {
        // Check if recruiter already has a company
        if (organizationRepository.existsByRecruiterUserId(recruiterUserId)) {
            throw new CustomException("Recruiter already has a company profile");
        }

        Organization organization = new Organization();
        organization.setName(request.name());
        organization.setWebsite(request.website());
        organization.setCity(request.city());
        organization.setContactEmail(request.contactEmail());
        organization.setDescription(request.description());
        organization.setRecruiterUserId(recruiterUserId);
        organization.setVerified(false);
        organization.setExtension(request.extension());

        Organization saved = organizationRepository.save(organization);
        
        // Log the creation
        auditLogService.log("COMPANY", saved.getId(), "CREATED", recruiterUserId);

        return mapToResponse(saved);
    }

    public OrganizationResponse updateOrganization(Long organizationId, OrganizationRequest request, Long recruiterUserId) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new CustomException("Organization not found"));

        // Check if the recruiter owns this organization
        if (!organization.getRecruiterUserId().equals(recruiterUserId)) {
            throw new CustomException("You can only update your own company profile");
        }

        organization.setName(request.name());
        organization.setWebsite(request.website());
        organization.setCity(request.city());
        organization.setContactEmail(request.contactEmail());
        organization.setDescription(request.description());
        organization.setExtension(request.extension());

        Organization updated = organizationRepository.save(organization);
        
        // Log the update
        auditLogService.log("COMPANY", updated.getId(), "UPDATED", recruiterUserId);

        return mapToResponse(updated);
    }

    public OrganizationResponse getOrganizationById(Long organizationId) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new CustomException("Organization not found"));
        
        return mapToResponse(organization);
    }

    public Optional<OrganizationResponse> getRecruiterOrganization(Long recruiterUserId) {
        return organizationRepository.findByRecruiterUserId(recruiterUserId)
                .map(this::mapToResponse);
    }

    public boolean hasOrganization(Long recruiterUserId) {
        return organizationRepository.existsByRecruiterUserId(recruiterUserId);
    }

    private OrganizationResponse mapToResponse(Organization organization) {
        return new OrganizationResponse(
                organization.getId(),
                organization.getName(),
                organization.getWebsite(),
                organization.getCity(),
                organization.getContactEmail(),
                organization.getDescription(),
                organization.getVerified(),
                organization.getCreatedAt(),
                organization.getUpdatedAt(),
                organization.getExtension()
        );
    }
}