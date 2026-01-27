package com.jobtracking.common.service;

import org.springframework.stereotype.Service;

import com.jobtracking.organization.repository.OrganizationRepository;
import com.jobtracking.profile.repository.RecruiterProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final OrganizationRepository organizationRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;

    /**
     * Check if recruiter's organization is verified
     */
    public boolean isRecruiterVerified(Long recruiterId) {
        return recruiterProfileRepository.findByUserId(recruiterId)
                .map(profile -> profile.isVerified())
                .orElse(false);
    }

    /**
     * Check if organization is verified
     */
    public boolean isOrganizationVerified(Long organizationId) {
        return organizationRepository.findById(organizationId)
                .map(org -> org.getVerified() != null && org.getVerified())
                .orElse(false);
    }

    /**
     * Check if recruiter owns the organization and it's verified
     */
    public boolean isRecruiterAuthorizedForOrganization(Long recruiterId, Long organizationId) {
        return organizationRepository.findById(organizationId)
                .map(org -> org.getRecruiterUserId().equals(recruiterId) && 
                           org.getVerified() != null && org.getVerified())
                .orElse(false);
    }
}