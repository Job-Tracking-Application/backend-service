package com.jobtracking.organization.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jobtracking.audit.service.AuditLogService;
import com.jobtracking.auth.entity.User;
import com.jobtracking.auth.repository.UserRepository;
import com.jobtracking.common.exception.AuthorizationException;
import com.jobtracking.common.exception.CustomException;
import com.jobtracking.common.exception.DuplicateEntityException;
import com.jobtracking.organization.dto.OrganizationRequest;
import com.jobtracking.organization.dto.OrganizationResponse;
import com.jobtracking.organization.entity.Organization;
import com.jobtracking.organization.repository.OrganizationRepository;
import com.jobtracking.profile.entity.RecruiterProfile;
import com.jobtracking.profile.repository.RecruiterProfileRepository;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final AuditLogService auditLogService;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final UserRepository userRepository;

    public OrganizationService(OrganizationRepository organizationRepository, 
                             AuditLogService auditLogService,
                             RecruiterProfileRepository recruiterProfileRepository,
                             UserRepository userRepository) {
        this.organizationRepository = organizationRepository;
        this.auditLogService = auditLogService;
        this.recruiterProfileRepository = recruiterProfileRepository;
        this.userRepository = userRepository;
    }

    public List<OrganizationResponse> getAllOrganizations() {
        return organizationRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public OrganizationResponse getOrganizationById(Long organizationId) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new CustomException("Organization not found"));
        
        return mapToResponse(organization);
    }

    public OrganizationResponse createOrganization(OrganizationRequest request, Long recruiterUserId) {
        // Check if recruiter already has a company
        if (organizationRepository.existsByRecruiterUserId(recruiterUserId)) {
            throw new DuplicateEntityException("Company", "recruiter " + recruiterUserId);
        }

        // Get the user to validate they exist and are a recruiter
        User user = userRepository.findById(recruiterUserId)
            .orElseThrow(() -> new CustomException("User not found"));
        
        if (user.getRoleId() != 2) {
            throw new AuthorizationException("create", "company - only recruiters can create companies");
        }

        // Create the organization
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
        
        // Create or update RecruiterProfile to link to this company
        RecruiterProfile recruiterProfile = recruiterProfileRepository.findByUserId(recruiterUserId)
            .orElse(new RecruiterProfile());
        
        // Set up the recruiter profile
        recruiterProfile.setUser(user);
        recruiterProfile.setCompany(saved);
        recruiterProfile.setVerified(false); // Will be verified when company is verified
        
        // Save the recruiter profile
        recruiterProfileRepository.save(recruiterProfile);
        
        // Log the creation
        auditLogService.log("COMPANY", saved.getId(), "CREATED", recruiterUserId);

        return mapToResponse(saved);
    }

    public OrganizationResponse updateOrganization(Long organizationId, OrganizationRequest request, Long recruiterUserId) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new CustomException("Organization not found"));

        // Check if the recruiter owns this organization
        if (!organization.getRecruiterUserId().equals(recruiterUserId)) {
            throw new AuthorizationException("update", "company profile");
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

    public Optional<OrganizationResponse> getRecruiterOrganization(Long recruiterUserId) {
        List<Organization> organizations = organizationRepository.findByRecruiterUserId(recruiterUserId);
        return organizations.isEmpty() ? 
            Optional.empty() : 
            Optional.of(mapToResponse(organizations.get(0)));
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