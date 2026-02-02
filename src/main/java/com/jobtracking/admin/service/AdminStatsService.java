package com.jobtracking.admin.service;

import org.springframework.stereotype.Service;

import com.jobtracking.admin.dto.AdminStatsResponse;

import lombok.RequiredArgsConstructor;

/**
 * Service for admin statistics operations
 * Follows Single Responsibility Principle - only handles statistics aggregation
 */
@Service
@RequiredArgsConstructor
public class AdminStatsService {

    private final AdminUserService adminUserService;
    private final AdminJobService adminJobService;
    private final AdminCompanyService adminCompanyService;
    private final AdminApplicationService adminApplicationService;

    /**
     * Get comprehensive admin statistics
     */
    public AdminStatsResponse getStats() {
        long userCount = adminUserService.getUserCount();
        long jobCount = adminJobService.getJobCount();
        long companyCount = adminCompanyService.getCompanyCount();
        long applicationCount = adminApplicationService.getApplicationCount();
        
        return new AdminStatsResponse(userCount, jobCount, companyCount, applicationCount);
    }

    /**
     * Get detailed statistics with additional metrics
     */
    public AdminStatsResponse getDetailedStats() {
        AdminStatsResponse basicStats = getStats();
        
        // Could be extended with additional metrics like:
        // - Active vs inactive users
        // - Verified vs unverified companies
        // - Applications by status
        // - Jobs by status
        
        return basicStats;
    }
}