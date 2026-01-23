package com.jobtracking.organization.service;

import com.jobtracking.organization.dto.OrganizationRequest;
import com.jobtracking.organization.dto.OrganizationResponse;
import com.jobtracking.organization.entity.Organization;
import com.jobtracking.organization.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    public OrganizationResponse create(OrganizationRequest request) {
        Organization org = new Organization();
        org.setName(request.getName());
        org.setWebsite(request.getWebsite());
        org.setCity(request.getCity());
        org.setContactEmail(request.getContactEmail());
        org.setExtension(request.getExtension());
        org.setVerified(false);

        Organization saved = organizationRepository.save(org);
        return toResponse(saved);
    }

    public OrganizationResponse update(Long id, OrganizationRequest request) {
        Organization org = organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        org.setName(request.getName());
        org.setWebsite(request.getWebsite());
        org.setCity(request.getCity());
        org.setContactEmail(request.getContactEmail());
        org.setExtension(request.getExtension());

        Organization updated = organizationRepository.save(org);
        return toResponse(updated);
    }

    public OrganizationResponse getById(Long id) {
        Organization org = organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        return toResponse(org);
    }

    private OrganizationResponse toResponse(Organization org) {
        return OrganizationResponse.builder()
                .id(org.getId())
                .name(org.getName())
                .website(org.getWebsite())
                .city(org.getCity())
                .contactEmail(org.getContactEmail())
                .verified(org.getVerified())
                .extension(org.getExtension())
                .createdAt(org.getCreatedAt())
                .build();
    }

}
