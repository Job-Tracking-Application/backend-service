package com.jobtracking.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jobtracking.admin.dto.AdminCompanyResponse;
import com.jobtracking.audit.service.AuditLogService;
import com.jobtracking.organization.repository.OrganizationRepository;
import com.jobtracking.common.utils.ValidationUtil;

import lombok.RequiredArgsConstructor;

/**
 * Service for admin company management operations
 * Follows Single Responsibility Principle - only handles company-related admin operations
 */
@Service
@RequiredArgsConstructor
public class AdminCompanyService {

    private final OrganizationRepository organizationRepository;
    private final AuditLogService auditLogService;

    /**
     * Get all companies for admin view
     */
    public List<AdminCompanyResponse> getAllCompanies() {
        return organizationRepository.findAll().stream()
                .map(company -> new AdminCompanyResponse(
                        company.getId(),
                        company.getName(),
                        company.getWebsite(),
                        company.getCity(),
                        company.getContactEmail(),
                        company.getVerified(),
                        company.getCreatedAt()))
                .toList();
    }

    /**
     * Update company verification status
     */
    public void updateCompanyVerification(Long companyId, Boolean verified, Long adminId) {
        ValidationUtil.validateNotNull(companyId, "Company ID cannot be null");
        ValidationUtil.validateNotNull(verified, "Verification status cannot be null");
        ValidationUtil.validateNotNull(adminId, "Admin ID cannot be null");

        organizationRepository.findById(companyId).ifPresentOrElse(company -> {
            Boolean oldStatus = company.getVerified();
            company.setVerified(verified);
            organizationRepository.save(company);
            
            String action = verified ? "VERIFIED" : "UNVERIFIED";
            auditLogService.log("COMPANY", companyId, action, adminId,
                "Company '" + company.getName() + "' verification changed from " + 
                oldStatus + " to " + verified);
        }, () -> {
            throw new IllegalArgumentException("Company not found with ID: " + companyId);
        });
    }

    /**
     * Get company count for statistics
     */
    public long getCompanyCount() {
        return organizationRepository.count();
    }

    /**
     * Get verified company count
     */
    public long getVerifiedCompanyCount() {
        return organizationRepository.countByVerifiedTrue();
    }

    /**
     * Get company by ID for admin view
     */
    public AdminCompanyResponse getCompanyById(Long companyId) {
        ValidationUtil.validateNotNull(companyId, "Company ID cannot be null");

        return organizationRepository.findById(companyId)
                .map(company -> new AdminCompanyResponse(
                        company.getId(),
                        company.getName(),
                        company.getWebsite(),
                        company.getCity(),
                        company.getContactEmail(),
                        company.getVerified(),
                        company.getCreatedAt()))
                .orElseThrow(() -> new IllegalArgumentException("Company not found with ID: " + companyId));
    }
}